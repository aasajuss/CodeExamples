#include <iostream>
#include <string>
#include <filesystem>
#include "opencv2\opencv.hpp"
#include "Dirent.h"
#include <cstring>
#include <cstdio>
#include <cerrno>
#include "Main.h"
#include "Windows.h"
#include "wtypes.h"
#include <algorithm>
#include <sys/stat.h>
#include <sys/types.h>
#include <stdio.h>
#include <process.h>
#include <direct.h>

using namespace std;
using namespace cv;

bool paused = false;
vector<string> fileNames;
char * currentFile;
const char * url = "res/source/";
string urlString = url;
const char * desturl = "res/";
string desturlString = desturl;
int orderNum = 0;
int playbackSpeed = 25; //default value
vector<string> filePreviewUrls;
vector<Mat> filePreviewMats;
vector<map<string, string>> actionHistory;
string currentFileType;
string mainWindowName;

void GetDesktopResolution(int& horizontal, int& vertical)
{
	RECT desktop;
	const HWND hDesktop = GetDesktopWindow();
	GetWindowRect(hDesktop, &desktop);
	horizontal = desktop.right;
	vertical = desktop.bottom;
}

int getFiles(const char * str) {

	DIR *dir;
	struct dirent *ent;
	vector<string> files;
	int emptyStringAmount = 2;
	bool hasFolders = false;
	bool hasUnicode;
	bool hasUnicodeFinal = false;
	if ((dir = opendir(str)) != NULL) {

		/* print all the files and directories within directory */
		while ((ent = readdir(dir)) != NULL) {
			if (emptyStringAmount <= 0) {

				string name = urlString + ent->d_name;
				LPCSTR lpname = name.c_str();

				hasUnicode = false;
				for (auto c : name) {
					if (static_cast<unsigned char>(c) > 127) {
						hasUnicode = true;
						hasUnicodeFinal = true;
					}
				}

				DWORD attrs = GetFileAttributes(lpname);
				if (attrs == 16) { // is a directory
					hasFolders = true;
				}
				else if (getFileType(ent->d_name) != "image" && getFileType(ent->d_name) != "video") { //if file extension doesnt fit

					string txt = "Left out file: " + name;
					cout << txt << endl;
				}
				else if (hasUnicode) {

					string txt = "Left out file with unicode path";
					cout << txt << endl;
				}
				else {
					files.push_back(ent->d_name);
				}

			}
			emptyStringAmount--;

		}

		closedir(dir);
		fileNames = files;
		if (hasFolders && hasUnicodeFinal) {
			MessageBox(nullptr, TEXT("Note: Skipped files with special characters and folders"), TEXT("Notification"), MB_OK);
		}
		else if (hasFolders) {
			MessageBox(nullptr, TEXT("Note: Skipped folders"), TEXT("Notification"), MB_OK);
		}
		else if (hasUnicodeFinal) {
			MessageBox(nullptr, TEXT("Note: Skipped files with special characters"), TEXT("Notification"), MB_OK);
		}

		return 1;
	}
	else {
		/* could not open directory */
		return -1;
	}

}

void addActionHistory(string action, string src, string dest) {

	map<string, string> act;
	act["action"] = action;
	act["src"] = src;
	act["dest"] = dest;
	actionHistory.insert(actionHistory.begin(), act);

}

map<string, string> getLatestAction() {

	map<string, string> temp = actionHistory.at(0);
	return temp;

}

void moveFile(int num, int destfolder) {

	char *cstr = &fileNames.at(num)[0u];
	string dest = desturlString + to_string(destfolder) + "/" + cstr;
	string onlyDestFolder = desturlString + to_string(destfolder);

	string src = urlString + cstr;
	const char * srcC = src.c_str();
	const char * destC = dest.c_str();

	if (std::rename(srcC, destC) < 0) {
		//std::cout << strerror(errno) << '\n';
		if (errno == 17) {
			MessageBox(nullptr, TEXT("Destination folder has a file with the same name"), TEXT("Message"), MB_OK);
		}
		else if (errno == 2) {
			//MessageBox(nullptr, TEXT("Destination folder doesn't exist"), TEXT("Message"), MB_OK);

			LPCSTR fname = onlyDestFolder.c_str();
			int i = _mkdir(fname);
			if (i == -1) {
				MessageBox(nullptr, TEXT("Tried to create a folder, but it failed!"), TEXT("Message"), MB_OK);
			}
			moveFile(num, destfolder);
		}
		else {
			MessageBox(nullptr, TEXT(strerror(errno)), TEXT("Message"), MB_OK);
		}
	}
	else {
		addActionHistory("move", src, dest);

	}

}

int folderExists(string url) {

	const char * pathname = url.c_str();
	struct stat info;

	if (stat(pathname, &info) != 0)
		printf("cannot access %s\n", pathname);
	else if (info.st_mode & S_IFDIR)  // S_ISDIR() doesn't exist on my windows 
		printf("%s is a directory\n", pathname);
	else
		printf("%s is no directory\n", pathname);

	return 0;

}

void adaptSize(int width, int height) {

	int desktopW = 0;
	int desktopH = 0;
	GetDesktopResolution(desktopW, desktopH);

	int newWidth = width;
	int newHeight = height;

	//first, resize window to a proper size

	while (newWidth > (desktopW - 100) || newHeight > (desktopH - 100)) {
		newWidth = (newWidth / 3 * 2.5);
		newHeight = (newHeight / 3 * 2.5);

	}

	resizeWindow(mainWindowName, newWidth, newHeight);

	//next, move window to the middle of screen

	desktopW /= 2;
	desktopH /= 2;
	desktopH -= (newHeight / 2 + 40);
	desktopW -= (newWidth / 2);

	moveWindow(mainWindowName, desktopW, desktopH);

}

string getFileType(string name) {

	string iexs[] = {
		"jpg",
		"jpeg",
		"jpe",
		"png",
		"tiff",
		"tif",
		"bmp",

	};
	vector<string> imageExtensions(iexs, iexs + (sizeof(iexs) / sizeof(*iexs)));

	for (int i = 0; i < imageExtensions.size(); i++) {
		string j = imageExtensions.at(i);
		string jup = j;
		transform(jup.begin(), jup.end(), jup.begin(), ::toupper);
		if (name.substr(name.find_last_of(".") + 1) == j || name.substr(name.find_last_of(".") + 1) == jup) {
			return "image";
		}
	}

	string vexs[] = {
		"mp4",
		"avi",
		"mov",
		"flv",
		"wmv",
		"webm",
		"mpeg",
		"mpg",
		"gif"

	};
	vector<string> videoExtensions(vexs, vexs + (sizeof(vexs) / sizeof(*vexs)));

	for (int i = 0; i < videoExtensions.size(); i++) {
		string k = videoExtensions.at(i);
		string kup = k;
		transform(kup.begin(), kup.end(), kup.begin(), ::toupper);
		if (name.substr(name.find_last_of(".") + 1) == k || name.substr(name.find_last_of(".") + 1) == kup) {
			return "video";
		}
	}

	return name.substr(name.find_last_of(".") + 1);
}

int resizeAllFiles() {

	//loading...

	MessageBox(nullptr, TEXT("Click OK to preload files"), TEXT("Message"), MB_OK);

	for (int i = 0; i < fileNames.size(); i++) {

		char * vidName = &fileNames.at(i)[0u];
		string name = urlString + vidName;
		string filetype;
		LPCSTR lpname = name.c_str();
		DWORD attrs = GetFileAttributes(lpname);

		if (attrs == 16) { // is a directory
			return 0;
		}


		filetype = getFileType(name);

		if (filetype == "image") {

			Mat img;
			img = imread(name);

			// resizing operation
			Mat smallimg;
			Size img_size = img.size();
			int img_height = img_size.height;
			int img_width = img_size.width;
			double percentage = 1000.0 / (img_width);
			double new_height = img_height * percentage;
			Size new_size = Size(1000.0, new_height);
			resize(img, smallimg, new_size, 0, 0, INTER_AREA); //if could use a bit more performance, use INTER_NEAREST

			filePreviewUrls.push_back(name);
			filePreviewMats.push_back(smallimg);

		}
	}

	MessageBox(nullptr, TEXT("Preloading finished!"), TEXT("Message"), MB_OK);

}

void cancelLastAction() {

	if (getLatestAction()["action"] == "move") {

		string a = getLatestAction()["dest"];
		string b = getLatestAction()["src"];
		const char * dest = a.c_str();
		const char * src = b.c_str();

		if (std::rename(dest, src) < 0) {
			std::cout << strerror(errno) << '\n';
		}
		else {

			actionHistory.erase(actionHistory.begin());
			orderNum--;
		}
	}

	else if (getLatestAction()["action"] == "init") {

		orderNum = 0;
		//maybe add message for "nothing to cancel" ? //
	}
	else {
		cout << "error occured:" << endl;
	}
}

int newFile(int number) {

	int loopInt = 0;
	char * vidName = &fileNames.at(number)[0u];

	string name = urlString + vidName;
	LPCSTR lpname = name.c_str();
	DWORD attrs = GetFileAttributes(lpname);

	if (attrs == 16) { // is a directory
		return 0;
	}

	namedWindow(mainWindowName, WINDOW_NORMAL);
	currentFileType = getFileType(name);

	if (currentFileType == "image") {

		Mat currentMat;

		for (int i = 0; i < filePreviewUrls.size(); i++) {
			if (name == filePreviewUrls[i]) {
				currentMat = filePreviewMats[i];
				i = filePreviewUrls.size();
			}
		}

		imshow(mainWindowName, currentMat);
		adaptSize(currentMat.cols, currentMat.rows);

		char key = (char)waitKey(0);

        /* can assign custom actions for each folder or button */

		if (key == 27) { //esc
			destroyAllWindows();
			return 0;
		}
		if (key == 49) { //1
			choose(1);
			return 0;
		}
		if (key == 50) { //2
			choose(2);
			return 0;
		}
		if (key == 51) { //3
			choose(3);
			return 0;
		}
		if (key == 52) { //4
			choose(4);
			return 0;
		}
		if (key == 53) { //5
			choose(5);
			return 0;
		}
		if (key == 54) { //6
			choose(6);
			return 0;
		}
		if (key == 55) { //7
			choose(7);
			return 0;
		}
		if (key == 56) { //8
			choose(8);
			return 0;
		}
		if (key == 57) { //9
			choose(9);
			return 0;
		}
		if (key == 8) { //backspace
			cancelLastAction();
			newFile(orderNum);
			return 0;
		}
	}

	if (currentFileType == "video") {

		VideoCapture cap(name);

		if (!cap.isOpened()) {
			MessageBox(nullptr, TEXT("Unknown error occured when opening source file"), TEXT("Message"), MB_OK);
			return -1;
		}

		while (true) {
			if (!paused) {

				loopInt++;
				Mat frame;
				cap >> frame;

				if (loopInt == 1) {
					adaptSize(frame.cols, frame.rows);
				}

				if (frame.empty()) {
					cap.open(name);
					cap >> frame;
					adaptSize(frame.cols, frame.rows);
					loopInt = 0;
				}

				imshow(mainWindowName, frame);
				char key = (char)waitKey(playbackSpeed);

                /* can assign custom actions for each folder or button */

				if (key == 27) { //esc
					cap.release();
					destroyAllWindows();
					return 0;
				}
				if (key == 49) { //1
					cap.release();
					choose(1);
					return 0;
				}
				if (key == 50) { //2
					cap.release();
					choose(2);
					return 0;
				}
				if (key == 51) { //3
					cap.release();
					choose(3);
					return 0;
				}
				if (key == 52) { //4
					cap.release();
					choose(4);
					return 0;
				}
				if (key == 53) { //5
					cap.release();
					choose(5);
					return 0;
				}
				if (key == 54) { //6
					cap.release();
					choose(6);
					return 0;
				}
				if (key == 55) { //7
					cap.release();
					choose(7);
					return 0;
				}
				if (key == 56) { //8
					cap.release();
					choose(8);
					return 0;
				}
				if (key == 57) { //9
					cap.release();
					choose(9);
					return 0;
				}
				if (key == 8) { //backspace
					cap.release();
					cancelLastAction();
					newFile(orderNum);
					return 0;
				}
			}
		}
	}

	else { //file not a supported file type

		string msg = "File type unsupported (." + currentFileType + "). Moving to next file";
		//MessageBox(nullptr, TEXT(msg.c_str()), TEXT("Message"), MB_OK);
		cout << msg << endl;

		orderNum++;
		if (orderNum != fileNames.size()) {
			newFile(orderNum);
		}
		else {
			MessageBox(nullptr, TEXT("Source folder is now empty. Click OK to exit"), TEXT("Message"), MB_OK);
		}

	}

	return 0;
}

void choose(int num) {

	moveFile(orderNum, num);
	orderNum++;
	if (orderNum != fileNames.size()) {
		newFile(orderNum);
	}
	else {
		MessageBox(nullptr, TEXT("Source folder is now empty. Click OK to exit"), TEXT("Message"), MB_OK);
	}
}

int init() {

	addActionHistory("init");
	mainWindowName = "MediaSort";

	if (getFiles(url) < 0) { //if folder cant be found
		MessageBox(nullptr, TEXT("Incorrect folder structure. Make sure a 'res/source' folder exists"), TEXT("Message"), MB_OK);
		return 0;
	}
	if (fileNames.size() == 0) { //empty source folder
		MessageBox(nullptr, TEXT("Source folder is empty"), TEXT("Message"), MB_OK);
	}
	else { //init first video
		resizeAllFiles();
		newFile(orderNum);
		cout << "finished" << endl;
	}
	return 0;
}


int main() {

	init();
	return EXIT_SUCCESS;

}
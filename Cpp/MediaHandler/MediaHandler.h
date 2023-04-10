#pragma once

#include <iostream>
#include <string>
#include <filesystem>
#include "opencv2\opencv.hpp"
#include "Dirent.h"
#include <cstring>
#include <cstdio>
#include <cerrno>

int handleControls(std::string ftype, cv::VideoCapture cap = NULL);
void setControls();
void choose(int num);
int newFile(int number);
std::string getFileType(std::string);
void addActionHistory(std::string action, std::string src = "empty", std::string dest = "empty");
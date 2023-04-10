let gameOver = false;
let lastRender = 0;
let downDown;
let leftDown;
let rightDown;
let upDown;
let spaceDown;
let lDown;
let hasControl = true;
let dlcdStart = 100;
let dlcd = dlcdStart;
let blockTypeStore = [];

let parent;

let cubeSize = 40;

let level = [

[0,0,0,0,0,0,0,0,0,0],
[0,0,0,0,0,0,0,0,0,0],
[0,0,0,0,0,0,0,0,0,0],
[0,0,0,0,0,0,0,0,0,0],
[0,0,0,0,0,0,0,0,0,0],
[0,0,0,0,0,0,0,0,0,0],
[0,0,0,0,0,0,0,0,0,0],
[0,0,0,0,0,0,0,0,0,0],
[0,0,0,0,0,0,0,0,0,0],
[0,0,0,0,0,0,0,0,0,0],
[0,0,0,0,0,0,0,0,0,0],
[0,0,0,0,0,0,0,0,0,0],
[0,0,0,0,0,0,0,0,0,0],
[0,0,0,0,0,0,0,0,0,0],
[0,0,0,0,0,0,0,0,0,0],
[0,0,0,0,0,0,0,0,0,0],
[0,0,0,0,0,0,0,0,0,0],

];

let canvasWidth = 400;
let canvasHeight = 680;

let levelX = level[0].length;
let levelY = level.length;

let procRoundCounter = 0;
let procRoundThreshold = 20;

let inputProcCounter = 0;
let inputProc = 3; 

let controlCoolDownCounter = 0;
let controlCoolDown = 1;
let currentBlockType = 0;
let currentBlockRotation = 0;

let mainContainer;
let ctx;
let mainContainerWidth = levelX * cubeSize;
let mainContainerHeight = levelY * cubeSize;

function init() {
	
	document.body.style.backgroundColor = "black";
	document.body.style.overflow = "hidden";
	parent = $('body');
	
	mainContainer = document.getElementById("mainCanvas");
	mainContainer.style.backgroundColor = "#10111e";
	mainContainer.style.position = "absolute";
	mainContainer.style.left = "700px";
	mainContainer.style.width = "300px";
	mainContainer.style.top = "50px";
	
	ctx = mainContainer.getContext("2d");
	
	updateGraphics();
	initControls();
	spawnBlock();
	
	window.requestAnimationFrame(loop);
}

function update() {
	
	if (!gameOver) {	
		checkInput();
		checkProcRound();
	}
}

function checkProcRound() {
	
	if (procRoundCounter > procRoundThreshold) {
		procRoundCounter = 0;
		moveBlock(1, 0);
	} else {
		procRoundCounter++;
	}	
}

function rotateBlock() {
	
	let YXs = [];
	
	for (let i = levelY - 1; i >= 0; i--) {
		for (let j = levelX - 1; j >= 0; j--) {
			if (level[i][j] > 0) YXs.push([i, j]);
		}
	}
	
	let potentialYXs = [];
	
	switch (currentBlockType) {
		case 0:
			let center = YXs[1];
			if (currentBlockRotation == 0 || currentBlockRotation == 2) {
				potentialYXs[3] = [center[0] - 1, center[1]];
				potentialYXs[2] = [center[0]    , center[1]];
				potentialYXs[1] = [center[0] + 1, center[1]];
				potentialYXs[0] = [center[0] + 2, center[1]];
			} else {
				potentialYXs[3] = [center[0], center[1] + 1];
				potentialYXs[2] = [center[0]    , center[1]];
				potentialYXs[1] = [center[0], center[1] - 1];
				potentialYXs[0] = [center[0], center[1] - 2];
			}
		break;
		case 1:
			let center = YXs[2];
			if (currentBlockRotation == 0) {
				potentialYXs[3] = [center[0] - 1, center[1]];
				potentialYXs[2] = [center[0], center[1]];
				potentialYXs[1] = [center[0] + 1, center[1]];
				potentialYXs[0] = [center[0] + 1, center[1] - 1];
			}
			if (currentBlockRotation == 1) {
				potentialYXs[3] = [center[0], center[1] + 1];
				potentialYXs[2] = [center[0], center[1]];
				potentialYXs[1] = [center[0], center[1] - 1];
				potentialYXs[0] = [center[0] - 1, center[1] - 1];
			}
			if (currentBlockRotation == 2) {
				potentialYXs[3] = [center[0] - 1, center[1]];
				potentialYXs[2] = [center[0], center[1]];
				potentialYXs[1] = [center[0] + 1, center[1]];
				potentialYXs[0] = [center[0] - 1, center[1] + 1];
			}
			if (currentBlockRotation == 3) {
				potentialYXs[3] = [center[0], center[1] - 1];
				potentialYXs[2] = [center[0], center[1]];
				potentialYXs[1] = [center[0], center[1] + 1];
				potentialYXs[0] = [center[0] + 1, center[1] + 1];
			}
		break;
		case 2:
			if (currentBlockRotation == 0) {
				let center = YXs[2];
				potentialYXs[3] = [center[0] - 1, center[1]];
				potentialYXs[2] = [center[0], center[1]];
				potentialYXs[1] = [center[0] + 1, center[1]];
				potentialYXs[0] = [center[0] - 1, center[1] - 1];
			}
			if (currentBlockRotation == 1) {
				let center = YXs[1];
				potentialYXs[3] = [center[0] - 1, center[1] + 1];
				potentialYXs[2] = [center[0], center[1] + 1];
				potentialYXs[1] = [center[0], center[1]];
				potentialYXs[0] = [center[0], center[1] - 1];
			}
			if (currentBlockRotation == 2) {
				let center = YXs[2];
				potentialYXs[3] = [center[0] - 1, center[1]];
				potentialYXs[2] = [center[0], center[1]];
				potentialYXs[1] = [center[0] + 1, center[1]];
				potentialYXs[0] = [center[0] + 1, center[1] + 1];
			}
			if (currentBlockRotation == 3) {
				let center = YXs[1];
				potentialYXs[3] = [center[0], center[1]];
				potentialYXs[2] = [center[0], center[1] + 1];
				potentialYXs[1] = [center[0], center[1] + 2];
				potentialYXs[0] = [center[0] + 1, center[1]];
			}	
		break;
		case 3:
		
		break;
		
		case 4:
			if (currentBlockRotation == 0) {
				let center = YXs[3];
				potentialYXs[3] = [center[0], center[1]];
				potentialYXs[2] = [center[0] + 1, center[1]];
				potentialYXs[1] = [center[0], center[1] - 1];
				potentialYXs[0] = [center[0] - 1, center[1] - 1];
			}
			if (currentBlockRotation == 1) {
				let center = YXs[1];
				potentialYXs[3] = [center[0] - 1, center[1] + 1];
				potentialYXs[2] = [center[0], center[1] - 1];
				potentialYXs[1] = [center[0], center[1]];
				potentialYXs[0] = [center[0] - 1, center[1]];
			}
			if (currentBlockRotation == 2) {
				let center = YXs[0];
				potentialYXs[3] = [center[0] - 1, center[1]];
				potentialYXs[2] = [center[0], center[1] + 1];
				potentialYXs[1] = [center[0] + 1, center[1] + 1];
				potentialYXs[0] = [center[0], center[1]];
			}
			if (currentBlockRotation == 3) {
				let center = YXs[3];
				potentialYXs[3] = [center[0], center[1]];
				potentialYXs[2] = [center[0] + 1, center[1]];
				potentialYXs[1] = [center[0] + 1, center[1] - 1];
				potentialYXs[0] = [center[0], center[1] + 1];
			}	
		break;
		
		case 5:
			if (currentBlockRotation == 0) {
				let center = YXs[2];
				potentialYXs[3] = [center[0], center[1] - 1];
				potentialYXs[2] = [center[0], center[1]];
				potentialYXs[1] = [center[0] + 1, center[1] - 1];
				potentialYXs[0] = [center[0] - 1, center[1]];
			}
			if (currentBlockRotation == 1) {
				let center = YXs[1];
				potentialYXs[3] = [center[0] - 1, center[1]];
				potentialYXs[2] = [center[0] - 1, center[1] - 1];
				
				potentialYXs[1] = [center[0], center[1]];
				potentialYXs[0] = [center[0], center[1] + 1];
			}
			if (currentBlockRotation == 2) {
				let center = YXs[2];
				potentialYXs[3] = [center[0] + 1, center[1]];
				potentialYXs[2] = [center[0], center[1]];
				potentialYXs[1] = [center[0], center[1] + 1];
				potentialYXs[0] = [center[0] - 1, center[1] + 1];
			}
			if (currentBlockRotation == 3) {
				let center = YXs[2];
				potentialYXs[3] = [center[0] + 1, center[1]];
				potentialYXs[2] = [center[0], center[1]];
				potentialYXs[1] = [center[0] + 1, center[1] + 1];
				potentialYXs[0] = [center[0], center[1] - 1];
			}	
		break;
		
		case 6:
			if (currentBlockRotation == 0) {
				let center = YXs[2];
				potentialYXs[3] = [center[0], center[1] - 1];
				potentialYXs[2] = [center[0], center[1]];
				potentialYXs[1] = [center[0] - 1, center[1]];
				potentialYXs[0] = [center[0] + 1, center[1]];
			}
			if (currentBlockRotation == 1) {
				let center = YXs[1];
				potentialYXs[3] = [center[0] - 1, center[1]];
				potentialYXs[2] = [center[0], center[1] - 1];
				potentialYXs[1] = [center[0], center[1]];
				potentialYXs[0] = [center[0], center[1] + 1];
			}
			if (currentBlockRotation == 2) {
				let center = YXs[1];
				potentialYXs[3] = [center[0] - 1, center[1]];
				potentialYXs[2] = [center[0], center[1] + 1];
				potentialYXs[1] = [center[0], center[1]];
				potentialYXs[0] = [center[0] + 1, center[1]];
			}
			if (currentBlockRotation == 3) {
				let center = YXs[2];
				potentialYXs[3] = [center[0], center[1] + 1];
				potentialYXs[2] = [center[0], center[1]];
				potentialYXs[1] = [center[0] + 1, center[1]];
				potentialYXs[0] = [center[0], center[1] - 1];
			}	
		break;
		
		default:
		
		break;
		
	}
		
	let legal = true;
	for (let i = 0; i < potentialYXs.length; i++) {
		if (outOfYBounds(potentialYXs[i][0], potentialYXs[i][1])) legal = false;
		if (outOfXBounds(potentialYXs[i][0], potentialYXs[i][1])) legal = false;
		if (legal) {
			if (blockAlreadyAt(potentialYXs[i][0], potentialYXs[i][1])) legal = false;
		}
	}
	
	if (legal) {
		for (let i = 0; i < potentialYXs.length; i++) {
			moveCube(YXs[i][0], YXs[i][1], potentialYXs[i][0], potentialYXs[i][1], true);
		}
		currentBlockRotation++;
		if (currentBlockRotation > 3) currentBlockRotation = 0;
	}
	
	
}

function spawnBlock(type = 0) {
	
	if (blockTypeStore.length == 7) blockTypeStore = [];
	
	do {
		type = getRandomInt(0,6);
		currentBlockType = type;
	} while (blockTypeStore.includes(type));
	
	blockTypeStore.push(type);
	currentBlockRotation = 0;
	
	switch (type) {
		
		case 0:
			level[0][3] = 1;
			level[0][4] = 1;
			level[0][5] = 1;
			level[0][6] = 1;
		break;
		case 1:
			level[0][3] = 2;
			level[0][4] = 2;
			level[0][5] = 2;
			level[1][5] = 2;
		break;
		case 2:
			level[0][3] = 3;
			level[1][3] = 3;
			level[0][4] = 3;
			level[0][5] = 3;
		break;
		case 3:
			level[0][3] = 4;
			level[0][4] = 4;
			level[1][3] = 4;
			level[1][4] = 4;
		break;
		case 4:
			level[0][4] = 5;
			level[0][5] = 5;
			level[1][3] = 5;
			level[1][4] = 5;
		break;
		case 5:
			level[0][3] = 6;
			level[0][4] = 6;
			level[1][4] = 6;
			level[1][5] = 6;
		break;
		case 6:
			level[0][3] = 7;
			level[0][4] = 7;
			level[0][5] = 7;
			level[1][4] = 7;
		break;
		default:
		
		break;
	}
	updateGraphics();
}

function moveBlock(y, x) {
	
	let legal = true;
	let shouldFreeze = false;
	
	for (let i = levelY - 1; i >= 0; i--) {
		for (let j = levelX - 1; j >= 0; j--) {
			if (level[i][j] > 0) {
				if (outOfXBounds(i + y, j + x)) legal = false;
				if (outOfYBounds(i + y, j + x)) shouldFreeze = true;
				if (!outOfXBounds(i + y, j + x) && !outOfYBounds(i + y, j + x)) {
					if (blockAlreadyAt(i + y, j + x)) shouldFreeze = true;
				}
			}
		}
	}
	
	if (legal && !shouldFreeze) {
		let cubes = [];
		for (let i = levelY - 1; i >= 0; i--) {
			for (let j = levelX - 1; j >= 0; j--) {
				if (level[i][j] > 0) {
					cubes.push([i, j]);
				}
			}
		}
		moveCubes(cubes, y, x);
	} else if (shouldFreeze && x == 0) {
		freezeBlock();
		spawnBlock();
	}
}

function freezeBlock() {
	
	for (let i = levelY - 1; i >= 0; i--) {
		for (let j = levelX - 1; j >= 0; j--) {
			if (level[i][j] > 0) {
				level[i][j] = -currentBlockType - 1;
			}
		}
	}
	removeFullRows();
	checkIfEnding();
}

function removeFullRows() {

	let fullYs = [];
	for (let i = levelY - 1; i >= 0; i--) {
		let full = true;
		for (let j = levelX - 1; j >= 0; j--) {
			if (level[i][j] > -1) full = false;
		}
		if (full) fullYs.push(i);
	}
	
	let highestY = 0;
	if (fullYs.length > 0) {
		for (let k = 0; k < fullYs.length; k++) {
			for (let i = 0; i < level[0].length; i++) {
				level[fullYs[k]][i] = 0;
				if (fullYs[k] > highestY) highestY = fullYs[k];
			}
		}
	}
	
	for (let k = 0; k < fullYs.length; k++) {
		for (let i = highestY - 1; i >= 0; i--) {
			for (let j = levelX - 1; j >= 0; j--) {
				level[i + 1][j] = level[i][j];
				level[i][j] = 0;
			}
		}
	}
	
	updateGraphics();
}

function outOfYBounds(endY, endX) {
	
	if (endY > levelY - 1 || endY < 0) return true;
	return false;
}

function outOfXBounds(endY, endX) {

	if (endX < 0 || endX > levelX - 1) return true;
	return false;
}

function blockAlreadyAt(endY, endX) {
	
	if (level[endY][endX] < 0) return true;
	return false;
}

function checkInput() {

	if (inputProcCounter > inputProc) {
		
		if (leftDown) moveBlock(0, -1);
		if (rightDown) moveBlock(0, 1);
		if (downDown) moveBlock(1, 0);
		if (upDown) rotateBlock();
		inputProcCounter = 0;
	} else {
		inputProcCounter++;
	}
}

function moveCube(startY, startX, dirY, dirX, absoluteDir = false) {

	level[startY][startX] = 0;
	if (!absoluteDir) {
		level[startY + dirY][startX + dirX] = currentBlockType + 1;
	} else {
		level[dirY][dirX] = currentBlockType + 1;
	}

	updateGraphics();
}

function moveCubes(cubes, dirY, dirX) {
	
	let copy = level;
	for (let i = levelY - 1; i >= 0; i--) {
		for (let j = levelX - 1; j >= 0; j--) {
			if (level[i][j] > 0) level[i][j] = 0;
		}
	}
	
	for (let i = 0; i < cubes.length; i++) {
		let y = cubes[i][0];
		let x = cubes[i][1];
		copy[y + dirY][x + dirX] = currentBlockType + 1;
	}
	level = copy;
	updateGraphics();
}

function checkIfEnding() {

	let ending = false;
	for (let i = 0; i < level[0].length; i++) {
		if (level[0][i] < 0) ending = true;
	}
	if (ending) endGame();
	
}

function endGame() {
	
	console.log("Game over!");
	hasControl = false;
	gameOver = true;
	document.onkeydown = null;
	$('#mainCanvas').remove();
	
}

function updateGraphics() {
	
	ctx.clearRect(0, 0, canvasWidth, canvasHeight);
	for (let i = levelY - 1; i >= 0; i--) {
		for (let j = levelX - 1; j >= 0; j--) {
			
			if (level[i][j] != 0) {
				
				if (level[i][j] == 1 || level[i][j] == -1) ctx.fillStyle = "#fff5af";
				if (level[i][j] == 2 || level[i][j] == -2) ctx.fillStyle = "#c34637";
				if (level[i][j] == 3 || level[i][j] == -3) ctx.fillStyle = "#197378";
				if (level[i][j] == 4 || level[i][j] == -4) ctx.fillStyle = "#64508c";
				if (level[i][j] == 5 || level[i][j] == -5) ctx.fillStyle = "#782d23";
				if (level[i][j] == 6 || level[i][j] == -6) ctx.fillStyle = "#6eb4f5";
				if (level[i][j] == 7 || level[i][j] == -7) ctx.fillStyle = "#9bbe87";
				
				ctx.fillRect(j * cubeSize, i * cubeSize, cubeSize, cubeSize);
			}
		}
	}
}

function initControls() {

	downDown = false;
	leftDown = false;
	rightDown = false;
	upDown = false;
	spaceDown = false;
	lDown = false;
	
	document.addEventListener('keyup', function (event) {
		
		if (event.code == "KeyL") {
			lDown = false;
		}
	
		if (hasControl) {
		
			if (event.code == "ArrowDown" || event.code == "KeyS") {
				downDown = false;
			}
			if (event.code == "ArrowLeft" || event.code == "KeyA") {
				leftDown = false;
			}
			if (event.code == "ArrowRight" || event.code == "KeyD") {
				rightDown = false;
			}
			if (event.code == "ArrowUp" || event.code == "KeyW") {
				upDown = false;
			}
			if (event.code == "Space") {
				spaceDown = false;
			}
		}
	}, true);
	
	document.addEventListener('keydown', function (event) {
		
		if (event.code == "KeyL") {
			lDown = true;
		} 
	
		if (hasControl) {
	
			if (event.code == "ArrowDown" || event.code == "KeyS") {
				downDown = true;
			} 
			if (event.code == "ArrowLeft" || event.code == "KeyA") {
				leftDown = true;
			}
			if (event.code == "ArrowRight" || event.code == "KeyD") {
				rightDown = true;
			}
			if (event.code == "ArrowUp" || event.code == "KeyW") {
				upDown = true;
			}
			if (event.code == "Space") {
				spaceDown = true;
			}
		}
	}, true);
}

function getRandomInt(min, max) {       
    let byteArray = new Uint16Array(1);
    window.crypto.getRandomValues(byteArray);

    let range = max - min + 1;
    let max_range = 2056;
    if (byteArray[0] >= Math.floor(max_range / range) * range)
        return getRandomInt(min, max);
    return min + (byteArray[0] % range);
}

function loop(timestamp) {
  if (!gameOver) {
	  let progress = timestamp - lastRender;

	  update(progress)

	  lastRender = timestamp
	  window.requestAnimationFrame(loop)
  }
}

init();
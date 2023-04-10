let lastRender = 0;
let startStamp = 0;
let parent;
let gameOver = false;
let canvasHeight = 300;
let canvasWidth = 450;
let leftPadding = 350;
let points = 3;
let player;
let enemy;
let playerHeight = 10;
let leftDown;
let rightDown;
let upDown;
let ball;
let ballXSpeed;
let ballYSpeed;
let ballInitX;
let ballInitY;
let ballSpeedInterval1 = 20;
let ballSpeedInterval2 = 30;
let maxXSpeed;
let ballSize = 20;
let invBall;
let blockX;
let area;
let powerup;
let powerupType = 0;
let powerupSize = 6;
let powerupStored = 0;
let powerupDiv;
let points1 = 0;
let points2 = 0;
let pointText1;
let pointText2;
let comment;
let commentI = 0;
let actionText;

let stringArr1 = [];
let stringArr2 = [];
let stringArr3 = [];
let stringArr4 = [];

let initReactionThreshold = 65;
let reactionThreshold = initReactionThreshold;
let reactionVariance = 3;

let blockVariance = [1, 20];
let duration = 10;

let playerWidth = 50;
let initBallYSpeed = 5;
let playerMoveSpeed = initBallYSpeed;

let activity = 0;
let roundActivity = 0;
let cheated = false;
let powerupFreq;
let lpowerups;

let firstTo = 5;

function init() {

	area = document.createElement("div");
	area.id = "area";
	area.style.left = leftPadding + "px";
	area.style.width = canvasWidth + "px";
	area.style.height = canvasHeight + "px";
	area.style.backgroundColor = "Black";
	area.style.top = "150px";
	area.style.position = "absolute";
	parent = area;
	
	pointText1 = document.createElement("div");
	pointText1.style.position = "absolute";
	pointText1.style.left = 20 + "px";
	pointText1.style.top = canvasHeight - 40 + "px";
	pointText1.innerHTML = points1;
	pointText1.style.color = "white";
	
	pointText2 = document.createElement("div");
	pointText2.style.position = "absolute";
	pointText2.style.left = 20 + "px";
	pointText2.style.top = 20 + "px";
	pointText2.innerHTML = points2;
	pointText2.style.color = "white";
	
	player = document.createElement("div");
	player.id = "player";
	player.style.left = (canvasWidth / 2 - 20) + "px";
	player.style.top = (canvasHeight - 5) + "px";
	player.style.width = playerWidth + "px";
	player.style.height = playerHeight + "px";
	player.style.position = "absolute";
	player.style.backgroundColor = "blue";
	
	enemy = document.createElement("div");
	enemy.id = "enemy";
	enemy.style.left = (canvasWidth / 2 - 20) + "px";
	enemy.style.top = "-5px";
	enemy.style.width = playerWidth + "px";
	enemy.style.height = playerHeight + "px";
	enemy.style.position = "absolute";
	enemy.mode = 0;
	enemy.stunned = false;
	enemy.blockCalculated = true;
	enemy.style.backgroundColor = "green";
	
	ballInitX = getRandomInt(30, 100);
	ballInitY = getRandomInt(10, 20);
	
	ball = document.createElement("div");
	ball.id = "ball";
	ball.style.backgroundColor = "White";
	ball.style.width = ballSize + "px";
	ball.style.height = ballSize + "px";
	ball.style.left = ballInitX + "px";
	ball.style.top = ballInitY + "px";
	ball.style.position = "absolute";
	ball.style.borderRadius = "10px";
	
	area.append(ball);
	area.append(player);
	area.append(enemy);
	$('html').append(area);
	parent.append(pointText1);
	parent.append(pointText2);
	
	powerupFreq = getRandomInt(3, 7);
	
	blockX = (canvasWidth / 2) - (playerWidth / 2);
	
	ballXSpeed = getRandomInt(ballSpeedInterval1,ballSpeedInterval2) / 10;
	ballYSpeed = initBallYSpeed;
	maxXSpeed = ballXSpeed;
	
	initControls();
}

function update(progress) {

	let ballX = parseFloat(ball.style.left);
	let ballY = parseFloat(ball.style.top);
	
	if (ballX > (canvasWidth - ballSize)) {
		ballX = (canvasWidth - ballSize) - 1;
		ballXSpeed = -ballXSpeed;
	}
	if (ballY > (canvasHeight - ballSize)) {
		respawnBall(enemy);
		return;
	}
	if (ballY < 0) {
		respawnBall(player);
		return;
	}
	if (ballX < 0) {
		ballX = 1;
		ballXSpeed = -ballXSpeed;
	}
	
	if (ballYSpeed < 0 && !enemy.blockCalculated) {
		let point = canvasHeight * (reactionThreshold / 100);
		let ballY = parseFloat(ball.style.top);
		
		if (ballY < point) calculateBlock();
	}
	
	checkCollision();
	
	ball.style.left = ballX + ballXSpeed + "px";
	ball.style.top = ballY + ballYSpeed + "px";
	
	checkAI();
	
	checkComment();
	
	if (leftDown) {
		rightDown = false;
		if (parseFloat(player.style.left) > 0) {	
			let prevX = parseFloat(player.style.left);	
			player.style.left = (prevX - playerMoveSpeed) + "px";
		}
	}
	else if (rightDown) {
		leftDown = false;
		if (parseFloat(player.style.left) < (canvasWidth - 40)) {		
			let prevX = parseFloat(player.style.left);	
			player.style.left = (prevX + playerMoveSpeed) + "px";
		}
	}
	
	if (upDown && powerupStored != 0) {
		usePowerup();
	}
  
}

function checkComment() {

	if (commentI > 250) {
		let lines = $('.person');
		lines[lines.length - 1].innerHTML = "";
	} else {
		commentI++;
	}
}

function changeText(text) {
		let lines = $('.person');
		lines[lines.length - 1].innerHTML = '"' + text + '"';
		commentI = 0;
	}

function usePowerup() {

	cheated = true;
	enemy.stunned = true;
	powerupStored = 0;
	roundActivity = 0;
	
	if ($('#powerup').length > 0) {
		$('#powerup').remove();
    }
	
	if ($('#actiontext').length > 0) {
		$('#actiontext').remove();
    }
	
	let stundur = 150;
	let r = getRandomInt(0, stringArr1.length - 1);
	r = stringArr1[r];
	if (powerupType == 2) {
		r = getRandomInt(0, stringArr2.length - 1);
		r = stringArr2[r];
		stundur = 300;
	}
	changeText(r);
	powerupType = 0;
	setTimeout(function() {
		enemy.stunned = false;
	}, stundur);
}

function checkAI() {

	let left = parseFloat(enemy.style.left);
	
	let diff = Math.abs(left - blockX);
	if (diff < playerMoveSpeed) return;
	
	if (!enemy.stunned) {
		if (left < blockX) {
			enemy.style.left = (left + playerMoveSpeed) + "px";
		} else {
			enemy.style.left = (left - playerMoveSpeed) + "px";
		}
	}
}

function createPowerup(type) {

	if ($('#smallpowerup').length > 0) return;

	powerup = document.createElement("div");
	powerup.style.width = powerupSize + "px";
	powerup.style.height = powerupSize + "px";
	powerup.id = "smallpowerup";
	powerup.style.borderRadius = powerupSize + "px";
	powerup.style.backgroundColor = "yellow";
	powerup.style.position = "absolute";
	powerup.style.top = player.style.top;
	
	let middle = canvasWidth / 2;
	let playerOnLeft = (parseFloat(player.style.left) + (playerWidth / 2)) < middle;
	let l = getRandomInt(30, (canvasWidth / 2 ));
	if (playerOnLeft) l = getRandomInt((canvasWidth / 2), canvasWidth - 30);
	powerup.style.left = l + "px";
	powerupType = type;
	if (type == 2) powerup.style.backgroundColor = "green";
	parent.append(powerup);

}

function calculateBlock() {
	
	let left = parseFloat(ball.style.left);
	let top = parseFloat(ball.style.top);
	
	let tempX = left;
	let tempY = top;
	let tempXSpeed = ballXSpeed;
	let tempYSpeed = ballYSpeed;
	
	let i = 0;
	
	while (tempY > 2) {
		if (tempX > (canvasWidth - ballSize)) {
			tempX = (canvasWidth - ballSize) - 1;
			tempXSpeed = -Math.abs(tempXSpeed);
		}
		if (tempY > (canvasHeight - 25)) {
			tempY = (canvasHeight - 25) - 1;
			tempYSpeed = -Math.abs(tempYSpeed);
		}
		if (tempX < 0) {
			tempX = 1;
			tempXSpeed = Math.abs(tempXSpeed);
		}
		tempX += tempXSpeed;
		tempY += tempYSpeed;
		
		if (i == 5) {
		i = 0;
		//createInd(tempX, tempY);
		} 
		i++;
	}
	
	tempX -= (playerWidth / 2);
	
	//variance
	let r = getRandomInt(blockVariance[0], blockVariance[1]);
	let s = getRandomInt(1,2);
	if (s == 1) tempX += r;
	if (s == 2) tempX -= r;
	
	if (tempX > (canvasWidth - playerWidth)) {
		tempX = canvasWidth - playerWidth;
	}
	if (tempX < 0) tempX = 0;
	
	blockX = tempX;
	enemy.blockCalculated = true;
}

function createInd(left, top) {

	let l = document.createElement("div");
	l.style.backgroundColor = "red";
	l.style.left = left + "px";
	l.style.top = top + "px";
	l.style.position = "absolute";
	l.style.height = "2px";
	l.style.width = "2px";
	parent.append(l);

}

function checkCollision() {

	let rectAX1 = parseFloat(player.style.left);
	let rectAX2 = parseFloat(player.style.left) + playerWidth;
	let rectAY1 = parseFloat(player.style.top);
	let rectAY2 = parseFloat(player.style.top) + playerHeight;
	
	let rectBX1 = parseFloat(ball.style.left);
	let rectBX2 = parseFloat(ball.style.left) + ballSize;
	let rectBY1 = parseFloat(ball.style.top);
	let rectBY2 = parseFloat(ball.style.top) + ballSize;
	
	let rectCX1 = parseFloat(enemy.style.left);
	let rectCX2 = parseFloat(enemy.style.left) + playerWidth;
	let rectCY1 = parseFloat(enemy.style.top);
	let rectCY2 = parseFloat(enemy.style.top) + playerHeight;
	
	if (powerupStored == 0 && powerupType != 0) {
		let rectDX1 = parseFloat(powerup.style.left);
		let rectDX2 = parseFloat(powerup.style.left) + powerupSize;
		
		if (rectAX1 < rectDX1 && rectAX2 > rectDX2) {
			consumePowerup(1);
		}
	}
	
	if (rectAX1 < rectBX2 && rectAX2 > rectBX1 &&
				rectAY1 < rectBY2 && rectAY2 > rectBY1) {
	
		reactionThreshold = initReactionThreshold;
		let r = (initReactionThreshold / reactionVariance);
		let rr = getRandomInt(1, r);
		reactionThreshold -= rr;
		newXDir(rectAX1, rectBX1);
		ballYSpeed = -Math.abs(ballYSpeed);
		activity++;
		if (powerupStored == 0) roundActivity++;
		if (roundActivity > powerupFreq && powerupStored == 0) {
			if (lewdPowerups) {
				createPowerup(2);
			} else {
				createPowerup(1);
			} 
		}
	}
	
	if (rectCX1 < rectBX2 && rectCX2 > rectBX1 &&
				rectCY1 < rectBY2 && rectCY2 > rectBY1) {
	
		newXDir(rectAX1, rectBX1);
		ballYSpeed = Math.abs(ballYSpeed);
		let mid = canvasWidth / 2 - playerWidth / 2;
		let diff = mid - rectCX1;
		blockX = mid - diff / 4;
	}
}

function consumePowerup(gained) {

	if (gained) {
		powerupStored = 1;
		
		powerupDiv = document.createElement("div");
		powerupDiv.style.left = "20px";
		powerupDiv.id = "powerup";
		powerupDiv.style.top = canvasHeight + 20 + "px";
		powerupDiv.style.position = "absolute";
		powerupDiv.style.backgroundColor = "yellow";
		powerupDiv.style.borderRadius = "10px";
		powerupDiv.style.width = "10px";
		powerupDiv.style.height = "10px";
		if (powerupType == 2) powerupDiv.style.backgroundColor = "green";
		parent.append(powerupDiv);
		
		actionText = document.createElement("div");
		actionText.style.position = "absolute";
		actionText.style.left = "40px";
		actionText.id = "actiontext";
		actionText.style.color = "white";
		actionText.style.fontFamily = "Garamond";
		actionText.style.letterSpacing = "2px";
		actionText.style.top = canvasHeight + 15 + "px";
		let r = getRandomInt(0, stringArr3.length - 1);
		r = stringArr3[r];
		if (powerupType == 2) {
			r = getRandomInt(0, stringArr4.length - 1);
			r = stringArr4[r];
		}
		actionText.innerHTML = "Powerup: " + r;
		parent.append(actionText);
	}
	if (powerupStored != 0) {
		powerup.remove();
	}
	
	if (!gained) {
		powerupType = 0;
	}
	roundActivity = 0;
}

function newXDir(px1, bx1) {

	let pone = playerWidth / 100;
	let xhalf = maxXSpeed / 50;
	bx1 += (ballSize / 2);
	
	let hitspot = bx1 - px1;
	let percent = hitspot - (playerWidth / 2);
	percent *= 5;
	if (percent > 150) percent = 150;
	if (percent < -150) percent = -150;
	
	let l = maxXSpeed / 100;
	ballXSpeed = l * percent;
	
	/*
	let r = getRandomInt(1,2);
	if (r == 1) ballXSpeed = -Math.abs(ballXSpeed);
	if (r == 2) ballXSpeed = Math.abs(ballXSpeed);
	*/
	
	enemy.blockCalculated = false;
}

function increaseSpeed() {

	let r = getRandomInt(1,2);
	let rr = getRandomInt(1,3) / 20;
	if (r == 1) {
		if (ballYSpeed > 0) ballYSpeed += rr;
		if (ballYSpeed < 0) ballYSpeed -= rr;
	}
	if (r == 2) {
		if (ballXSpeed > 0) ballXSpeed += rr;
		if (ballXSpeed < 0) ballXSpeed -= rr;
	}
}

function respawnBall(winner) {

	if (winner == player) {
		points1++;
		pointText1.innerHTML = points1;
	} else {
		points2++;
		pointText2.innerHTML = points2;
	}
	
	powerupFreq = getRandomInt(3, 7);
	consumePowerup(0);
	if ($('#powerup').length > 0) {
		$('#powerup').remove();
    }
	if ($('#smallpowerup').length > 0) {
		$('#smallpowerup').remove();
    }
	if ($('#actiontext').length > 0) {
		$('#actiontext').remove();
    }
	
	powerupStored = 0;
	roundActivity = 0;

	blockX = canvasWidth / 2 - playerWidth / 2;
	ballInitX = getRandomInt(30, 100);
	ballInitY = getRandomInt(10, 20);
	ballXSpeed = getRandomInt(ballSpeedInterval1,ballSpeedInterval2) / 10;
	ballYSpeed = initBallYSpeed;
	maxXSpeed = ballXSpeed;
	
	player.style.left = (canvasWidth / 2 - 20) + "px";
	enemy.style.left = (canvasWidth / 2 - 20) + "px";
	
	ball.style.left = ballInitX + "px";
	ball.style.top = ballInitY + "px";
	
}

function initControls() {

	leftDown = false;
	rightDown = false;
	
	document.addEventListener('keyup', function (event) {
		if (event.code == "ArrowLeft" || event.code == "KeyA") {
			leftDown = false;
		}
		else if (event.code == "ArrowRight" || event.code == "KeyD") {
			rightDown = false;
		}
		else if (event.code == "ArrowUp" || event.code == "KeyW") {
			upDown = false;
		}
	}, true);
	
	document.addEventListener('keydown', function (event) {
		if (event.code == "ArrowLeft" || event.code == "KeyA") {
			leftDown = true;
		} 
		else if (event.code == "ArrowRight" || event.code == "KeyD") {
			rightDown = true;
		}
		else if (event.code == "ArrowUp" || event.code == "KeyW") {
			upDown = true;
		}
	}, true);
}

function loop(timestamp) {
  if (!gameOver) {
  
	  if (lastRender == 0) {
		startStamp = timestamp;
	  }
	  let progress = timestamp - lastRender;

	  update(progress);

	  lastRender = timestamp;
	  
	  if (points1 >= firstTo || points2 >= firstTo) {
		let winner = points1 > points2 ? 1 : 2;
		gameOver = true;
		destroyAll();
	  }
  
	  window.requestAnimationFrame(loop);
  }  
}

 

function destroyAll() {

	$('#area').remove();
	$('#ball').remove();
	$('#player').remove();
	$('#enemy').remove();
	document.removeEventListener('keydown', function (event) {
		if (event.code == "ArrowLeft") {
			leftDown = true;
		} 
		if (event.code == "ArrowRight") {
			rightDown = true;
		}
		if (event.code == "ArrowUp") {
			upDown = true;
		}
	}, false);
	document.removeEventListener('keyup', function (event) {
		if (event.code == "ArrowLeft") {
			leftDown = false;
		}
		if (event.code == "ArrowRight") {
			rightDown = false;
		}
		if (event.code == "ArrowUp") {
			upDown = false;
		}
	}, false);
	
}

function begin() {

    init();

    setTimeout(function() {
        window.requestAnimationFrame(loop)
    }, 2000);

}

begin();
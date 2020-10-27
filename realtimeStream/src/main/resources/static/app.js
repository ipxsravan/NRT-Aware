

var ws;
function setConnected(connected) {
	
	$("#devicelocationData").html("");
	$("#alertEventData").html("");
}



function showMessage(message) {
	
	   $("#devicelocationData").prepend(message + "<br/>\n"+"<br/>");
	}

function showAlertData(message) {
	
	   $("#alertEventData").prepend(message + "<br/>\n"+"<br/>");
	}

$(function() {
	
	$("form").on('submit', function(e) {
		e.preventDefault();
	});
	
	$("#connect").click(function() {
		connect();
	});
	$("#disconnect").click(function() {
		disconnect();
	});
	
	$("#alert-connect").click(function() {
		alertDataConnect();
	});
	$("#alert-disconnect").click(function() {
		disconnect();
	});
	
});


function connect() {
	
	var socket = new WebSocket("ws://192.168.22.236:8080/awareData");
	ws = Stomp.over(socket);

	ws.connect({}, function(frame) {
		ws.subscribe("/topic/devicelocationData", function(message) {
			showMessage(message.body);
		});
	}, function(error) {
		$("#devicelocationData").append(error + "<br />\n");
	});
}

function disconnect() {
	if (ws != null) {
		ws.close();
	}
	setConnected(false);
	console.log("Disconnected");
}


function alertDataConnect(){
	//var socket = new WebSocket("ws://localhost:8080/alertEventData");
	var socket = new WebSocket("ws://192.168.22.236:8080/alertEventData");
	ws = Stomp.over(socket);

	ws.connect({}, function(frame) {
		ws.subscribe("/topic/alertEventTopic", function(message) {
			showAlertData(message.body);
		});
	}, function(error) {
		$("#alertEventData").append(error + "<br />\n");
	});
}
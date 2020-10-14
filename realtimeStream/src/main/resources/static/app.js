

var ws;
function setConnected(connected) {
	$("#connect").prop("disabled", connected);
	$("#disconnect").prop("disabled", !connected);
	
	$("#devicelocationData").html("");
}



function showMessage(message) {
	
	   $("#devicelocationData").prepend(message + "<br/>\n"+"<br/>");
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
	
});


function connect() {
	//connect to stomp where stomp endpoint is exposed
	var socket = new WebSocket("ws://192.168.22.236:8080/awareData");
	ws = Stomp.over(socket);

	ws.connect({}, function(frame) {
		ws.subscribe("/topic/devicelocationData", function(message) {
		});

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

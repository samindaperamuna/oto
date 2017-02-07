Twilio.Device.setup(token, {
	region : "sg1",
	debug : true
});

/* Let us know when the client is ready. */
Twilio.Device.ready(function(device) {
	$("#log").text("Ready");
});

/* Report any errors on the screen */
Twilio.Device.error(function(error) {
	$("#log").text("Error: " + error.message);
});

Twilio.Device.connect(function(conn) {
	$("#log").text("Successfully established call");
});

/* Log a message when a call disconnects. */
Twilio.Device.disconnect(function(conn) {
	$("#log").text("Call ended");
});

/* Listen for incoming connections */
Twilio.Device.incoming(function(conn) {
	$("#log").text("Incoming connection from " + conn.parameters.From);
	// accept the incoming connection and start two-way audio
	conn.accept();
});

/* Connect to Twilio when we call this function. */
function call() {
	Twilio.Device.connect();
}

/* A function to end a connection to Twilio. */
function hangup() {
	Twilio.Device.disconnectAll();
}
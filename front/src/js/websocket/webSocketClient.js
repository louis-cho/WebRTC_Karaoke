let socket;
let chatRoomId;
let senderId;

document.getElementById('connectButton').addEventListener('click', connectWebSocket);
document.getElementById('sendMessageButton').addEventListener('click', sendMessage);

function connectWebSocket() {
    chatRoomId = document.getElementById('chatRoomIdInput').value.trim();
    senderId = document.getElementById('senderIdInput').value.trim();

    if (!chatRoomId || !senderId) {
        console.error('Chat Room ID and Sender ID are required.');
        return;
    }

    console.log('Chat Room ID:', chatRoomId);
    console.log('Sender ID:', senderId);

    const url = `ws://localhost:8081/chat`;
    socket = new WebSocket(url);

    socket.onopen = (event) => {
        console.log('WebSocket connected:', event);
        sendEnterMessage();
        document.getElementById('connectButton').disabled = true;
    };

    socket.onmessage = (event) => {
        const message = JSON.parse(event.data);
        console.log('Received message:', message);
        displayMessage(message.senderId, message.message);
    };

    socket.onclose = (event) => {
        console.log('WebSocket closed:', event);
    };
}

function sendEnterMessage() {
    const message = {
        messageType: 'ENTER',
        chatRoomId: parseInt(chatRoomId),
        senderId: parseInt(senderId),
        message: '입장',
    };
    socket.send(JSON.stringify(message));
}

function sendMessage() {
    const messageInput = document.getElementById('messageInput');
    const messageText = messageInput.value.trim();

    if (!messageText) {
        console.error('Message cannot be empty.');
        return;
    }

    const message = {
        messageType: 'TALK',
        chatRoomId: parseInt(chatRoomId),
        senderId: parseInt(senderId),
        message: messageText,
    };
    socket.send(JSON.stringify(message));
    messageInput.value = '';
}

function displayMessage(senderId, message) {
    const chatOutput = document.getElementById('chatOutput');
    const newMessage = document.createElement('p');
    newMessage.textContent = `User ${senderId}: ${message}`;
    chatOutput.appendChild(newMessage);
}

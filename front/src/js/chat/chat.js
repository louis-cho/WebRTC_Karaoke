import app from "../config/preference.js";

let pref = app;
let stompClient;
let roomId;
let sender;

function connectToChat(roomId, sender) {

    if (roomId.trim() === '' || sender.trim() === '') {
        alert('Room ID와 Sender를 입력하세요.');
        return;
    }

    const socket = new WebSocket('ws://localhost:8081/api/ws');
    stompClient = Stomp.over(socket);

    stompClient.connect({}, () => {
        document.getElementById('connectButton').disabled = true;
        document.getElementById('messageForm').style.display = 'block';

        stompClient.subscribe(`/exchange/chat.exchange/room.${roomId}`, (message) => {
            handleIncomingMessage(JSON.parse(message.body));
        });

        loadOldMessages();
    });
}

function loadOldMessages() {
    const xhr = new XMLHttpRequest();

    xhr.onreadystatechange = function () {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status === 200) {
                const oldMessages = JSON.parse(xhr.responseText);
                displayOldMessages(oldMessages);
            } else {
                console.error('이전 메시지 불러오기 실패:', xhr.status, xhr.statusText);
            }
        }
    };

    xhr.open('GET', `/chat/room/${roomId}/oldMsg`);
    xhr.send();
}

function displayOldMessages(messages) {
    const chatMessagesDiv = document.getElementById('chatMessages');

    messages.reverse().forEach(message => {
        const messageElement = document.createElement('p');
        messageElement.textContent = `${message.sender}: ${message.message}`;
        chatMessagesDiv.appendChild(messageElement);
    });

    chatMessagesDiv.scrollTop = chatMessagesDiv.scrollHeight;
}

document.getElementById('messageForm').addEventListener('submit', (event) => {
    event.preventDefault();

    const messageInput = document.getElementById('message');
    const message = messageInput.value;

    if (message.trim() !== '') {
        const chatMessage = {
            type: 'TALK',
            roomId: roomId,
            sender: sender,
            message: message,
            time: ''
        };

        stompClient.send(`/pub/chat.message.${roomId}`, {}, JSON.stringify(chatMessage));
        messageInput.value = '';
    }
});

function handleIncomingMessage(message) {
    if (message) {
        displayMessage(message);
    }
}

function displayMessage(message) {
    const chatMessagesDiv = document.getElementById('chatMessages');
    const messageElement = document.createElement('p');
    messageElement.textContent = `${message.sender}: ${message.message}`;
    chatMessagesDiv.appendChild(messageElement);

    chatMessagesDiv.scrollTop = chatMessagesDiv.scrollHeight;
}

export async function fetchChatRoomList(userPk) {
    // const serverUrl = "/api/v1/chatroom/list/{userId}"
  const serverUrl = pref.app.api.protocol + pref.app.api.host + pref.app.api.chat.room.list + userPk;

  try {
    const response = await fetch(serverUrl, {
      method: 'GET',
    });

    const result = await response.json();
    console.log(result);

    return result.content;
  } catch (error) {
    console.error('Error:', error);
    return null;
  }
}

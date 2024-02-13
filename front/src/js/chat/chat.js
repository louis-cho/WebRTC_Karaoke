import app from "../config/preference.js";
import useCookie from "@/js/cookie.js";
const { setCookie, getCookie, removeCookie } = useCookie();
let pref = app;
let stompClient;
let roomId;
let sender;

let typingIndicatorTimer; // 전역 변수로 타이머를 저장

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

document.getElementById('messageForm').addEventListener('change', (event) => {
  event.preventDefault();
  const typingMessage = {
    type: 'TYPE',
    roomId: rommId,
    sender: sender,
  };

  stompClient.send(`/pub/.message.${roomId}`, {}, JSON.stringify(typingMessage));
});


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
    if (message.type == "TALK") {
        displayMessage(message);
    } else if(message.type == "TYPE") {
      displayTyping(message);
    }
}

function removeTypingIndicator(chatMessagesDiv) {
  // 타이핑 표시 엘리먼트 제거
  const typingIndicator = chatMessagesDiv.querySelector('p');
  if (typingIndicator) {
    chatMessagesDiv.removeChild(typingIndicator);
  }
}

function displayTyping(message) {
  const chatMessagesDiv = document.getElementById('chatMessages');

  // 이전 타이머가 있다면 취소
  if (typingIndicatorTimer) {
    clearTimeout(typingIndicatorTimer);
  }

  // 기존 타이핑 표시 엘리먼트가 있다면 제거
  removeTypingIndicator(chatMessagesDiv);

  // 특별한 텍스트로 타이핑 중임을 나타냄
  const typingIndicator = document.createElement('p');
  typingIndicator.textContent = `${message.sender}: ...`;
  chatMessagesDiv.appendChild(typingIndicator);

  // 스크롤을 가장 아래로 이동
  chatMessagesDiv.scrollTop = chatMessagesDiv.scrollHeight;

  // 1초 후에 특별한 텍스트를 제거
  typingIndicatorTimer = setTimeout(() => {
    removeTypingIndicator(chatMessagesDiv);
  }, 1000);
}

function displayMessage(message) {
    // 이전 타이머가 있다면 취소
    if (typingIndicatorTimer) {
      clearTimeout(typingIndicatorTimer);
    }


    // 기존 타이핑 표시 엘리먼트가 있다면 제거
    removeTypingIndicator(chatMessagesDiv);

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
      headers: {
        Authorization: getCookie("Authorization"),
        refreshToken: getCookie("refreshToken"),
        "Content-Type": "application/json",
      },
    });

    const result = await response.json();
    console.log(result);

    return result.content;
  } catch (error) {
    console.error('Error:', error);
    return null;
  }
}

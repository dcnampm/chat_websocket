'use strict';

const usernamePage = document.querySelector('#username-page');
const chatPage = document.querySelector('#chat-page');
const usernameForm = document.querySelector('#usernameForm');
const messageForm = document.querySelector('#messageForm');
const messageInput = document.querySelector('#message');
const connectingElement = document.querySelector('.connecting');
const chatArea = document.querySelector('#chat-messages');
const logout = document.querySelector('#logout');

let stompClient = null;
let nickname = null;
let fullname = null;
let password = null;
let selectedUserId = null;
let token = null;

function loginUser(event) {
    const nickname = document.querySelector('#nickname').value.trim();
    const password = document.querySelector('#password').value.trim();

    if (nickname && password) {
        const user = {
            nickName: nickname,
            password: password
        };

        // Gửi yêu cầu POST đến API để kiểm tra đăng nhập
        fetch('/api/v1/auth/authenticate', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(user)
        })
            .then(response => {
                if (response.ok) {
                    // Xử lý phản hồi nếu đăng nhập thành công
                    token = response.body;
                    return response.json();

                } else {
                    // Xử lý phản hồi nếu đăng nhập không thành công
                    throw new Error('Login failed');
                }
            })
            .then(data => {
                console.log('Login successful:', data);

                // Lưu token vào local storage hoặc cookie
                localStorage.setItem('token', data.token);

                // token = data.token;
                //
                // usernamePage.classList.add('hidden');
                // chatPage.classList.remove('hidden');
                //
                // // Tạo một đối tượng Cookie với token
                // const tokenCookie = "token=" + token;
                //
                // // Đặt cookie với miền cụ thể, ở đây là localhost
                // document.cookie = tokenCookie + "; domain=localhost; path=/";
                //
                // const socket = new SockJS('/ws');
                // stompClient = Stomp.over(socket);
                //
                // // Gửi cookies trong header của yêu cầu
                // const headers = {
                //     // Cookie: tokenCookie
                //     Authorization: 'Bearer ' + token
                // };
                //
                // stompClient.connect(headers, onConnected, onError);

                // Gọi hàm connect để kết nối tới WebSocket sau khi đăng nhập thành công
                connect(event);
            })
            .catch(error => {
                console.error('Error logging in:', error);
                // Xử lý lỗi đăng nhập
            });
    }
    event.preventDefault();
}

function connect(event) {
    nickname = document.querySelector('#nickname').value.trim();
    password = document.querySelector('#password').value.trim();

    if (nickname && password) {
        usernamePage.classList.add('hidden');
        chatPage.classList.remove('hidden');

        // Tạo một đối tượng Cookie với token
        const tokenCookie = "token=" + token;

        // Đặt cookie với miền cụ thể, ở đây là localhost
        document.cookie = tokenCookie + "; domain=localhost; path=/";

        const socket = new SockJS('/ws');
        stompClient = Stomp.over(socket);

        // Gửi cookies trong header của yêu cầu
        const headers = {
            Cookie: tokenCookie
            // Authorization: 'Bearer ' + token
        };

        stompClient.connect({}, onConnected, onError);
    }
    event.preventDefault();
}


function onConnected() {
    stompClient.subscribe(`/user/${nickname}/queue/messages`, onMessageReceived);
    stompClient.subscribe(`/user/public`, onMessageReceived);

    // register the connected user
    stompClient.send("/app/user.addUser",
        {},
        JSON.stringify({nickName: nickname, fullName: fullname, password: password, status: 'ONLINE'})
    );
    document.querySelector('#connected-user-fullname').textContent = fullname;
    findAndDisplayConnectedUsers().then();
}

async function findAndDisplayConnectedUsers() {
    const connectedUsersResponse = await fetch('/users');
    let connectedUsers = await connectedUsersResponse.json();
    connectedUsers = connectedUsers.filter(user => user.nickName !== nickname);
    const connectedUsersList = document.getElementById('connectedUsers');
    connectedUsersList.innerHTML = '';

    connectedUsers.forEach(user => {
        appendUserElement(user, connectedUsersList);
        if (connectedUsers.indexOf(user) < connectedUsers.length - 1) {
            const separator = document.createElement('li');
            separator.classList.add('separator');
            connectedUsersList.appendChild(separator);
        }
    });
}

function appendUserElement(user, connectedUsersList) {
    const listItem = document.createElement('li');
    listItem.classList.add('user-item');
    listItem.id = user.nickName;

    const userImage = document.createElement('img');
    userImage.src = '../img/user_icon.png';
    userImage.alt = user.fullName;

    const usernameSpan = document.createElement('span');
    usernameSpan.textContent = user.fullName;

    const receivedMsgs = document.createElement('span');
    receivedMsgs.textContent = '0';
    receivedMsgs.classList.add('nbr-msg', 'hidden');

    listItem.appendChild(userImage);
    listItem.appendChild(usernameSpan);
    listItem.appendChild(receivedMsgs);

    listItem.addEventListener('click', userItemClick);

    connectedUsersList.appendChild(listItem);
}

function userItemClick(event) {
    document.querySelectorAll('.user-item').forEach(item => {
        item.classList.remove('active');
    });
    messageForm.classList.remove('hidden');

    const clickedUser = event.currentTarget;
    clickedUser.classList.add('active');

    selectedUserId = clickedUser.getAttribute('id');
    fetchAndDisplayUserChat().then();

    const nbrMsg = clickedUser.querySelector('.nbr-msg');
    nbrMsg.classList.add('hidden');
    nbrMsg.textContent = '0';

}

function displayMessage(senderId, content) {
    const messageContainer = document.createElement('div');
    messageContainer.classList.add('message');
    if (senderId === nickname) {
        messageContainer.classList.add('sender');
    } else {
        messageContainer.classList.add('receiver');
    }
    const message = document.createElement('p');
    message.textContent = content;
    messageContainer.appendChild(message);
    chatArea.appendChild(messageContainer);
}

async function fetchAndDisplayUserChat() {
    const userChatResponse = await fetch(`/messages/${nickname}/${selectedUserId}`);
    const userChat = await userChatResponse.json();
    chatArea.innerHTML = '';
    userChat.forEach(chat => {
        displayMessage(chat.senderId, chat.content);
    });
    chatArea.scrollTop = chatArea.scrollHeight;
}


function onError() {
    connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
    connectingElement.style.color = 'red';
}


function sendMessage(event) {
    const messageContent = messageInput.value.trim();
    if (messageContent && stompClient) {
        const chatMessage = {
            senderId: nickname,
            recipientId: selectedUserId,
            content: messageInput.value.trim(),
            timestamp: new Date()
        };
        stompClient.send("/app/chat", {}, JSON.stringify(chatMessage));
        displayMessage(nickname, messageInput.value.trim());
        messageInput.value = '';
    }
    chatArea.scrollTop = chatArea.scrollHeight;
    event.preventDefault();
}


async function onMessageReceived(payload) {
    await findAndDisplayConnectedUsers();
    console.log('Message received', payload);
    const message = JSON.parse(payload.body);
    if (selectedUserId && selectedUserId === message.senderId) {
        displayMessage(message.senderId, message.content);
        chatArea.scrollTop = chatArea.scrollHeight;
    }

    if (selectedUserId) {
        document.querySelector(`#${selectedUserId}`).classList.add('active');
    } else {
        messageForm.classList.add('hidden');
    }

    const notifiedUser = document.querySelector(`#${message.senderId}`);
    if (notifiedUser && !notifiedUser.classList.contains('active')) {
        const nbrMsg = notifiedUser.querySelector('.nbr-msg');
        nbrMsg.classList.remove('hidden');
        nbrMsg.textContent = '';
    }
}

function onLogout() {
    stompClient.send("/app/user.disconnectUser",
        {},
        JSON.stringify({nickName: nickname, fullName: fullname, password: password, status: 'OFFLINE'})
    );
    window.location.reload();
}

usernameForm.addEventListener('submit', loginUser, true); // step 1
messageForm.addEventListener('submit', sendMessage, true);
logout.addEventListener('click', onLogout, true);
window.onbeforeunload = () => onLogout();
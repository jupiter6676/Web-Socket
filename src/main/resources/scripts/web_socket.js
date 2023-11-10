var ws;

function wsOpen() {
    // Web Socket을 지정한 URL로 연결 (WebSocketConfig)
    ws = new WebSocket("ws://127.0.0.1/web-socket")
    wsEvent();
}

function wsEvent() {
    // 소켓이 열리면 동작
    ws.onopen = function(e) { }

    // 서버로부터 데이터 수신
    ws.onmessage = function(res) {
        var rawData = res.data; // 전달받은 데이터
        if (rawData !== null && rawData.trim() != "") {
            var data = JSON.parse(rawData);

            // 1. 소켓 연결 시 sessionId 세팅
            if (data.type === "getId") {
                var sessionId = data.sessionId !== null ? d.sessionId : "";
                if (sessionId !== null) {
                    var obj = {
                        type: "open",
                        sessionId: document.querySelector("#sessionId").value,
                        userName: document.querySelector("#userName").value
                    };

                    // 서버에 데이터 전송
                    ws.send(JSON.stringify(obj));
                }
            }

            // 2. 채팅 메시지를 전달받은 경우
            else if (data.type === "message") {
                var chatBox = document.querySelector("#chatting");

                if (data.sessionId === document.querySelector("#sessionId")) {
                    chatBox.insertAdjacentHTML("beforeend", `<p class=me>${data.msg}</p>`);
                } else {
                    chatBox.insertAdjacentHTML("beforeend", `<p class=others>${data.userName}: ${data.msg}</p>`);
                }
            }

            // 3. 새로운 유저가 입장한 경우
            else if (data.type === "open") {
                var chatBox = document.querySelector("#chatting");

                if (data.sessionId === document.querySelector("#sessionId")) {
                    chatBox.insertAdjacentHTML("beforeend", `<p class=start>[채팅에 참가하였습니다.]</p>`);
                } else {
                    chatBox.insertAdjacentHTML("beforeend", `<p class=start>[${data.userName}님이 입장하였습니다.]</p>`);
                }
            }

            // 4. 유저가 퇴장한 경우
            else if (data.type === "close") {
                var chatBox = document.querySelector("#chatting");
                chatBox.insertAdjacentHTML("beforeend", `<p class=exit>[${data.userName}님이 퇴장하였습니다.]</p>`);
            }

            else {
                console.warn("Unknown type!");
            }
        }
    }

    document.addEventListener("keypress", function(e) {
        if (e.keyCode === 13) { // Enter
            send();
        }
    });
}

function chatName() {
    var userName = document.querySelector("#userName");

    if (userName.value === null || userName.value.trim() === "") {
        alert("사용자 이름을 입력해주세요.");
        userName.focus();
    } else {
        wsOpen();
        userName.style.display = "none";
        userName.style.display = "block";
    }
}

function send() {
    var obj ={
        type: "message",
        sessionId: document.querySelector("#sessionId").value,
        userName: document.querySelector("#userName").value,
        msg: document.querySelector("#chat").value
    };

    //서버에 데이터 전송
    ws.send(JSON.stringify(obj))
    document.querySelector("#chat").innerText = "";
}
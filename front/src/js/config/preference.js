let app = {};

app.api = {};

app.api.prefix = "/api/v1";

app.api.socket = "wss:"; //ec2
app.api.protocol = "https:";
app.api.host = "//i10a705.p.ssafy.io" + app.api.prefix; // ec2
app.api.websocket = app.api.socket + "//i10a705.p.ssafy.io";

// app.api.socket = "ws:";
// app.api.protocol = "http:";
// app.api.host = "//localhost:8081" + app.api.prefix;
// app.api.websocket = app.api.socket + "//localhost:8081";
app.api.user = {};
app.api.user.login = "/user/login";
app.api.user.register = "/user/register";
app.api.user.fetch = "/user/get/";
app.api.user.update = "/user/update";
app.api.user.search = "/user/search/";
app.api.user.getUserPK = "/user/getPk";

app.api.comment = {};
app.api.comment.fetch = "/comment/feed/";
app.api.comment.count = "/comment/get/count/";
app.api.comment.add = "/comment/create";

app.api.feed = {};
app.api.feed.fetchAll = "/feed/get/all";
app.api.feed.fetchOld = "/feed/get/old";
app.api.feed.fetchTop100 = "/feed/get/top100";
app.api.feed.fetchOne = "/feed/get/";
app.api.feed.getByUser = "/feed/getUser/";
app.api.feed.delete = "/feed/delete/";
app.api.feed.update = "/feed/update/";

app.api.chat = {};
app.api.chat.room = {};
app.api.chat.room.list = "/chatroom/list/";
app.api.chat.send = "/pub/chat.message";
app.api.chat.old = "/oldMsg";
app.api.chat.new = "/newMsg";
app.api.chat.subscribe = "/exchange/chat.exchange/room.";

app.api.song = {};
app.api.song.fetch = "/song/";

app.api.hit = {};
app.api.hit.count = "/hit/get/";
app.api.hit.create = "/hit/create";

app.api.like = {};
app.api.like.count = "/like/get/";
app.api.like.create = "/like/create";
app.api.like.delete = "/like/delete";
app.api.like.clicked = "/like/clicked/";

app.api.friends = {};
app.api.friends.get = "/friends";
app.api.friends.list = "/list/";
app.api.friends.request = "/friends/request/";
app.api.friends.incomingRequest = "/friends/incoming-requests";
app.api.friends.count = "/count";
app.api.friends.accept = "/friends/accept/";
app.api.friends.delete = "/friends/delete/";

app.kor = {};

app.kor.friends = {};
app.kor.friends.manage = "친구 관리";
app.kor.friends.search = "친구 검색";
(app.kor.friends.request = "친구 삭제"),
  (app.kor.friends.accept = "친구 수락"),
  (app.kor.friends.reject = "친구 거절"),
  (app.kor.mainPage = {});
app.kor.mainPage.title = "노래해방";
app.kor.mainPage.content = "실시간 노래방 서비스";

app.kor.loginPage = {};
app.kor.loginPage.title = "로그인";
app.kor.loginPage.idLabel = "id";
app.kor.loginPage.idHint = "id를 입력해주세요";
app.kor.loginPage.pwLabel = "pw";
app.kor.loginPage.pwHint = "pw를 입력해주세요";

app.kor.karaoke = {};
app.kor.karaoke.list = {};
app.kor.karaoke.list.sessionList = "노래방 목록";
app.kor.karaoke.list.createSession = "방 만들기";
app.kor.karaoke.list.sessionId = "방 제목";
app.kor.karaoke.list.numberOfParticipants = "인원";
app.kor.karaoke.list.status = "상태";
app.kor.karaoke.list.private = "비밀방";
app.kor.karaoke.list.public = "공개방";
app.kor.karaoke.list.manager = "방장";
app.kor.karaoke.list.joinSession = "입장하기";
app.kor.karaoke.list.recording = "노래중";
app.kor.karaoke.list.waiting = "대기중";
app.kor.karaoke.list.maxNumber = "(최대 6명)";
app.kor.karaoke.list.password = "비밀번호";
app.kor.karaoke.list.close = "닫기";
app.kor.karaoke.session = {};
app.kor.karaoke.session.invite = "초대";
app.kor.karaoke.session.setting = "설정";
app.kor.karaoke.session.leave = "나가기";
app.kor.karaoke.session.update = "변경하기";
app.kor.karaoke.session.chatting = "채팅";
app.kor.karaoke.session.input = "입력 설정";
app.kor.karaoke.session.reserve = "예약하기";
app.kor.karaoke.session.search = "검색어를 입력하세요";
app.kor.karaoke.session.reserveList = "예약목록";
app.kor.karaoke.session.message = "전달할 내용을 입력하세요";
app.kor.karaoke.session.send = "전송";
app.kor.karaoke.session.kick = "강퇴";
app.kor.karaoke.session.start = "시작";
app.kor.karaoke.session.stop = "취소";
app.kor.karaoke.session.cameraOn = "카메라 켜기";
app.kor.karaoke.session.cameraOff = "카메라 끄기";
app.kor.karaoke.session.mute = "음소거";
app.kor.karaoke.session.unmute = "음소거 해제";
app.kor.karaoke.session.applyEcho = "에코 적용하기";
app.kor.karaoke.session.removeEcho = "에코 해제하기";

app.eng = {};

app.eng.mainPage = {};
app.eng.mainPage.title = "Noraehaebang";

export default { app };

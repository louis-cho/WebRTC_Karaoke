let app = {};

app.api = {};

app.api.prefix = "/api/v1";
app.api.protocol = "https:";
app.api.host = "//i10a705.p.ssafy.io" + app.api.prefix; // ec2
// app.api.protocol = "http:";
// app.api.host = "//localhost:8081" + app.api.prefix;

app.api.socket = "ws:";

app.api.user = {};
app.api.user.login = "/user/login";
app.api.user.register = "/user/register";

app.api.comment = {};
app.api.comment.fetch = "/comment/feed/";

app.api.chat = {};
app.api.chat.room = {};
app.api.chat.room.list = "/chatroom/list/";
app.api.chat.send = "/pub/chat.message";
app.api.chat.old = "/oldMsg";
app.api.chat.new = "/newMsg";
app.api.chat.subscribe = "/exchange/chat.exchange/room.";

app.api.hit = {};
app.api.hit.count = "/hit/get/";

app.api.like = {};
app.api.like.count = "/like/get/";

app.kor = {};

app.kor.mainPage = {};
app.kor.mainPage.title = "노래해방";
app.kor.mainPage.content = "실시간 노래방 서비스";

app.kor.loginPage = {};
app.kor.loginPage.title = "로그인";
app.kor.loginPage.idLabel = "id";
app.kor.loginPage.idHint = "id를 입력해주세요";
app.kor.loginPage.pwLabel = "pw";
app.kor.loginPage.pwHint = "pw를 입력해주세요";

app.kor.karaokePage = {};
app.kor.karaokePage.title = "바로 지금 노래해방!";
app.kor.karaokePage.sessionId = "방 제목";
app.kor.karaokePage.userName = "참가자 이름";
app.kor.karaokePage.numberOfParticipants = "인원(최대 6명)";
app.kor.karaokePage.password = "비밀번호";
app.kor.karaokePage.public = "비공개";
app.kor.karaokePage.joinSession = "참가하기";
app.kor.karaokePage.leaveSession = "방 나가기";
app.kor.karaokePage.updateSession = "변경하기";
app.kor.karaokePage.applyFilter = "필터 적용하기";
app.kor.karaokePage.removeFilter = "필터 해제하기";
app.kor.karaokePage.filterList = "적용할 필터를 고르세요";

app.eng = {};

app.eng.mainPage = {};
app.eng.mainPage.title = "Noraehaebang";

export default { app };

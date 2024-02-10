let app = {};

app.api = {};

app.api.prefix = "/api/v1";
// app.api.protocol = "https:";
// app.api.host = "//i10a705.p.ssafy.io" + app.api.prefix; // ec2
app.api.socket = "ws:";
app.api.protocol = "http:";
app.api.host = "//localhost:8081" + app.api.prefix;

app.api.user = {};
app.api.user.login = "/user/login";
app.api.user.register = "/user/register";
app.api.user.fetch = "/user/get/";
app.api.user.update = "/user/update/";

app.api.comment = {};
app.api.comment.fetch = "/comment/feed/";
app.api.comment.count = "/comment/get/count/";

app.api.feed = {};
app.api.feed.fetchAll = "/feed/get/all";
app.api.feed.fetchOne = "/feed/get/";

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

app.api.like = {};
app.api.like.count = "/like/get/";


app.api.friends = {};
app.api.friends.get = "/friends/";
app.api.friends.list = "/list/";
app.api.friends.request = "/friends/request/";
// app.api.friends.accept = "/friends/accept/";
// app.api.friends.delete = "/friends/delete/";
// app.api.friends.incoming.requests = "/friends/incoming-requests/";
// app.api.friends.outgoing.requests = "/friends/outgoing-requests/";

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

app.kor.karaoke = {};
app.kor.karaoke.list = {};
app.kor.karaoke.list.sessionList = "노래방 목록";
app.kor.karaoke.list.createSession = "방 만들기";
app.kor.karaoke.list.sessionId = "방 제목";
app.kor.karaoke.list.numberOfParticipants = "인원";
app.kor.karaoke.list.status = "상태";
app.kor.karaoke.list.private = "비공개";
app.kor.karaoke.list.public = "공개";
app.kor.karaoke.list.joinSession = "입장하기";
app.kor.karaoke.list.recording = "녹화 중";
app.kor.karaoke.list.waiting = "대기 중";
app.kor.karaoke.list.maxNumber = "(최대 6명)";
app.kor.karaoke.list.password = "비밀번호";
app.kor.karaoke.list.close = "닫기";
app.kor.karaoke.session = {};
app.kor.karaoke.session.setting = "설정";
app.kor.karaoke.session.leave = "나가기";
app.kor.karaoke.session.update = "변경하기";
app.kor.karaoke.session.chatting = "채팅";
app.kor.karaoke.session.input = "입력 설정";
app.kor.karaoke.session.reserve = "예약하기";
app.kor.karaoke.session.reserveList = "예약목록";
app.kor.karaoke.session.message = "전달할 내용을 입력하세요";
app.kor.karaoke.session.send = "전송";
app.kor.karaoke.session.kick = "강퇴";
app.kor.karaoke.session.start = "시작";
app.kor.karaoke.session.stop = "취소";

app.kor.karaokePage = {};
app.kor.karaokePage.applyFilter = "필터 적용하기";
app.kor.karaokePage.removeFilter = "필터 해제하기";
app.kor.karaokePage.filterList = "적용할 필터를 고르세요";

app.eng = {};

app.eng.mainPage = {};
app.eng.mainPage.title = "Noraehaebang";

export default { app };

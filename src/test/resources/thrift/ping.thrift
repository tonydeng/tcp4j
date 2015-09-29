namespace java com.github.tonydeng.tcp4j.service

struct Ping {
    1: string message;
}

struct Pong {
    1: string answer;
}

service PingPongService {
    Pong knock(1: Ping ping);
}

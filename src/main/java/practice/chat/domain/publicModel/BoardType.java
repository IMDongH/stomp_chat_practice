package practice.chat.domain.publicModel;

public enum BoardType {

    RECRUIT(1,"구인"),
    CASTING_AUDITION(2,"캐스팅/오디션"),
    COMMUNITY(4,"커뮤니티"),
    ARTWORK(5,"작업물"),
    PORTFOLIO(6,"포트폴리오"),
    GENERAL(3,"일반");

    private long id;
    private String type;

    BoardType(long id, String type) {
        this.id = id;
        this.type = type;
    }
}

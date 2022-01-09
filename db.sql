create sequence SEQUENCE_GENERATOR
    increment by 50;
create table AUTHOR
(
    ID          NUMBER(38)    not null
        constraint PK_AUTHOR
            primary key,
    NAME        VARCHAR2(255) not null,
    SURNAME     VARCHAR2(255),
    BIRTHDAY    DATE,
    DIED        DATE,
    NATIONALITY VARCHAR2(255)
);
create table BOOK
(
    ID          NUMBER(38)    not null
        constraint PK_BOOK
            primary key,
    TITLE       VARCHAR2(255) not null,
    NUM_OF_PAGE NUMBER,
    COVER_URL   VARCHAR2(255),
    PUBLISHER   VARCHAR2(255),
    AUTHOR_ID   NUMBER(38)
        constraint FK_BOOK__AUTHOR_ID
            references AUTHOR
);
create table BORROWING
(
    ID             NUMBER(38) not null
        constraint PK_BORROWING
            primary key,
    BORROWING_DATE DATE,
    DELIVERY_DATE  DATE,
    JHI_COMMENT    CLOB,
    USER_ID        NUMBER(38)
        constraint FK_BORROWING__USER_ID
            references JHI_USER,
    BOOK_ID        NUMBER(38)
        constraint FK_BORROWING__BOOK_ID
            references BOOK
);
create table DATABASECHANGELOG
(
    ID            VARCHAR2(255) not null,
    AUTHOR        VARCHAR2(255) not null,
    FILENAME      VARCHAR2(255) not null,
    DATEEXECUTED  TIMESTAMP(6)  not null,
    ORDEREXECUTED NUMBER        not null,
    EXECTYPE      VARCHAR2(10)  not null,
    MD5SUM        VARCHAR2(35),
    DESCRIPTION   VARCHAR2(255),
    COMMENTS      VARCHAR2(255),
    TAG           VARCHAR2(255),
    LIQUIBASE     VARCHAR2(20),
    CONTEXTS      VARCHAR2(255),
    LABELS        VARCHAR2(255),
    DEPLOYMENT_ID VARCHAR2(10)
);
create table DATABASECHANGELOGLOCK
(
    ID          NUMBER    not null
        constraint PK_DATABASECHANGELOGLOCK
            primary key,
    LOCKED      NUMBER(1) not null,
    LOCKGRANTED TIMESTAMP(6),
    LOCKEDBY    VARCHAR2(255)
)|
create table JHI_AUTHORITY
(
    NAME VARCHAR2(50) not null
        constraint PK_JHI_AUTHORITY
            primary key
);
create table JHI_PERSISTENT_TOKEN
(
    SERIES      VARCHAR2(20) not null
        constraint PK_JHI_PERSISTENT_TOKEN
            primary key,
    USER_ID     NUMBER(38)
        constraint FK_USER_PERSISTENT_TOKEN
            references JHI_USER,
    TOKEN_VALUE VARCHAR2(20) not null,
    TOKEN_DATE  DATE,
    IP_ADDRESS  VARCHAR2(39),
    USER_AGENT  VARCHAR2(255)
);
create table JHI_USER
(
    ID                 NUMBER(38)   not null
        constraint PK_JHI_USER
            primary key,
    LOGIN              VARCHAR2(50) not null
        constraint UX_USER_LOGIN
            unique,
    PASSWORD_HASH      VARCHAR2(60) not null,
    FIRST_NAME         VARCHAR2(50),
    LAST_NAME          VARCHAR2(50),
    EMAIL              VARCHAR2(191)
        constraint UX_USER_EMAIL
            unique,
    IMAGE_URL          VARCHAR2(256),
    ACTIVATED          NUMBER(1)    not null,
    LANG_KEY           VARCHAR2(10),
    ACTIVATION_KEY     VARCHAR2(20),
    RESET_KEY          VARCHAR2(20),
    CREATED_BY         VARCHAR2(50) not null,
    CREATED_DATE       TIMESTAMP(6) default NULL,
    RESET_DATE         TIMESTAMP(6),
    LAST_MODIFIED_BY   VARCHAR2(50),
    LAST_MODIFIED_DATE TIMESTAMP(6)
);
create table JHI_USER_AUTHORITY
(
    USER_ID        NUMBER(38)   not null
        constraint FK_USER_ID
            references JHI_USER,
    AUTHORITY_NAME VARCHAR2(50) not null
        constraint FK_AUTHORITY_NAME
            references JHI_AUTHORITY,
    primary key (USER_ID, AUTHORITY_NAME)
);
create or replace PROCEDURE KUTUPHANE.BOOK_NUMBER(
    userId in number,
    borrowsNumber out number
)
is
BEGIN
   select count(*) into borrowsNumber from KUTUPHANE.BORROWING where USER_ID = userId and DELIVERY_DATE is null;
EXCEPTION
    WHEN OTHERS THEN
        dbms_output.put_line(SQLERRM);
END;

create or replace trigger KUTUPHANE.check_borrowings
    before insert
    on KUTUPHANE.BORROWING
    FOR EACH ROW
declare
    borrowingCount INT;
begin

    KUTUPHANE.BOOK_NUMBER(:new.USER_ID, borrowingCount);

    if (borrowingCount > 2) then
        raise_application_error(-10000, 'Aynı anda üçten fazla kitap ödünç alınamaz.');
    end if;
end;
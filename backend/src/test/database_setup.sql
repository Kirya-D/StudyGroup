IF OBJECT_ID('Choice', 'U') IS NOT NULL
BEGIN
    DROP TABLE Choice;
END

IF OBJECT_ID('Question', 'U') IS NOT NULL
BEGIN
    DROP TABLE Question;
END

IF OBJECT_ID('AccountStudyguideStatus', 'U') IS NOT NULL
BEGIN
    DROP TABLE AccountStudyguideStatus;
END

IF OBJECT_ID('Studyguide', 'U') IS NOT NULL
BEGIN
    DROP TABLE Studyguide;
END

IF OBJECT_ID('Account', 'U') IS NOT NULL
BEGIN
    DROP TABLE Account;
END

CREATE TABLE Account (
    id NVARCHAR(36) NOT NULL,
    username NVARCHAR(32) UNIQUE NOT NULL,
    password NVARCHAR(32) NOT NULL

    CONSTRAINT pkey_account PRIMARY KEY (id),
    CONSTRAINT unique_username UNIQUE (username)
);

CREATE TABLE Studyguide (
    id NVARCHAR(36) NOT NULL,
    title NVARCHAR(255) NOT NULL,
    description NVARCHAR(255) NOT NULL,
    idAccount NVARCHAR(36) DEFAULT NULL,


    CONSTRAINT pkey_studyguide PRIMARY KEY (id),
    CONSTRAINT fkey_studyguide_account FOREIGN KEY (idAccount) REFERENCES Account(id)
        ON DELETE SET NULL
);

CREATE TABLE AccountStudyguideStatus (
    idAccount NVARCHAR(36) NOT NULL,
    idStudyguide NVARCHAR(36) NOT NULL,
    favorited BIT NOT NULL DEFAULT (0),
    downloaded BIT NOT NULL DEFAULT (0),

    CONSTRAINT pkey_accountstudyguidestatus PRIMARY KEY (idAccount, idStudyguide),
    CONSTRAINT fkey_accountstudyguidestatus_account FOREIGN KEY (idAccount) REFERENCES Account(id)
        ON DELETE CASCADE,
    CONSTRAINT fkey_accountstudyguidestatus_studyguide FOREIGN KEY (idStudyguide) REFERENCES Studyguide(id)
        ON DELETE CASCADE
);

CREATE TABLE Question (
    id INT IDENTITY(1, 1),
    text NVARCHAR(255) NOT NULL,
    idStudyguide NVARCHAR(36) NOT NULL,

    CONSTRAINT pkey_question PRIMARY KEY (id),
    CONSTRAINT fkey_question_studyguide FOREIGN KEY (idStudyguide) REFERENCES Studyguide(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE Choice (
    id INT IDENTITY(1, 1),
    text NVARCHAR(255) NOT NULL,
    isAnswer BIT NOT NULL,
    idQuestion INT NOT NULL,

    CONSTRAINT pkey_choice PRIMARY KEY (id),
    CONSTRAINT fkey_choice_question FOREIGN KEY (idQuestion) REFERENCES Question(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

INSERT INTO Account (id, username, password)
    VALUES ('1', 'testUser', 'password');
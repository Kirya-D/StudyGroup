CREATE TABLE Account (
    id INT IDENTITY(1, 1),
    username NVARCHAR(32) UNIQUE NOT NULL,
    password NVARCHAR(32) NOT NULL

    CONSTRAINT pkey_account PRIMARY KEY (id),
    CONSTRAINT unique_username UNIQUE (username)
);

CREATE TABLE Studyguide (
    id INT IDENTITY(1, 1),
    title NVARCHAR(255) NOT NULL,
    description NVARCHAR(255) NOT NULL,
    idAccount INT DEFAULT NULL,


    CONSTRAINT pkey_studyguide PRIMARY KEY (id),
    CONSTRAINT fkey_studyguide_account FOREIGN KEY (idAccount) REFERENCES Account(id)
        ON DELETE SET NULL
);

CREATE TABLE AccountStudyguideStatus (
    idAccount INT NOT NULL,
    idStudyguide INT NOT NULL,
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
    idStudyguide INT NOT NULL,

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

INSERT INTO Account (username, password)
    VALUES ('testUser', 'password');
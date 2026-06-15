PRAGMA foreign_keys = ON;

CREATE TABLE Account (
    id INTEGER PRIMARY KEY,
    username NVARCHAR(32) UNIQUE NOT NULL,
    password NVARCHAR(32) NOT NULL
);

CREATE TABLE Studyguide (
    id INTEGER PRIMARY KEY,
    title NVARCHAR(255) NOT NULL,
    description NVARCHAR(255) NOT NULL,
    idAccount INT DEFAULT NULL,

    FOREIGN KEY (idAccount) REFERENCES Account(id)
        ON DELETE SET DEFAULT
        ON UPDATE CASCADE
);

CREATE TABLE AccountStudyguideStatus (
    idAccount INT NOT NULL,
    idStudyguide INT NOT NULL,
    favorited BOOLEAN NOT NULL,
    downloaded BOOLEAN NOT NULL,

    PRIMARY KEY (idAccount, idStudyguide),
    FOREIGN KEY (idAccount) REFERENCES Account(id),
    FOREIGN KEY (idStudyguide) REFERENCES Studyguide(id)
);

CREATE TABLE Question (
    id INTEGER PRIMARY KEY,
    text NVARCHAR(255) NOT NULL,
    idStudyguide INT NOT NULL,

    FOREIGN KEY (idStudyguide) REFERENCES Studyguide(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE Choice (
    id INTEGER PRIMARY KEY,
    text NVARCHAR(255) NOT NULL,
    isAnswer BOOLEAN NOT NULL,
    idQuestion INT NOT NULL,

    FOREIGN KEY (idQuestion) REFERENCES Question(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

INSERT INTO Account (username, password)
    VALUES ("testUser", "password");
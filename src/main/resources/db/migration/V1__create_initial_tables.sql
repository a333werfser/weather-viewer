CREATE TABLE Users (
    ID bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    Login varchar(25) UNIQUE NOT NULL,
    Password varchar(255) NOT NULL
);

CREATE TABLE Locations (
    ID bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    Name varchar(30) NOT NULL,
    UserId bigint NOT NULL REFERENCES Users(ID),
    Latitude decimal NOT NULL,
    Longitude decimal NOT NULL
);

CREATE TABLE Sessions (
    ID UUID UNIQUE NOT NULL PRIMARY KEY,
    UserId bigint UNIQUE NOT NULL REFERENCES Users(ID),
    ExpiresAt timestamp NOT NULL
);
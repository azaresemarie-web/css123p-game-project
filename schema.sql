CREATE DATABASE if not exists FormulaHeroLB;

CREATE table if not exists LEVEL(
    level_id int AUTO_INCREMENT PRIMARY KEY,
    level_number int not null,
    level_name varchar(100) not null,
    description varchar(255)
);

CREATE table if not exists GAME_SESSION(
    session_id int AUTO_INCREMENT PRIMARY key,
    player_name varchar(100) not null,
    level_id int not null,
    start_time datetime default CURRENT_TIMESTAMP,
    end_time datetime null,
    lives_remaining int not null,
    session_status enum('IN_PROGRESS', 'COMPLETED', 'FAILED') default 'INT_PROGRESS',
    FOREIGN key (level_id) REFERENCES LEVEL(level_id) on delete CASCADE
);

CREATE table if not exists QUESTION(
	question_id int AUTO_INCREMENT PRIMARY key,
    level_id int not null,
    difficulty enum('EASY','MEDIUM','HARD') not null,
    question_text varchar(255) not null,
    correct_answer varchar(100) not null,
    topic_category varchar(100) not null,
    time_limit_seconds int not null,
    FOREIGN key (level_id) REFERENCES LEVEL(level_id) on delete CASCADE
);

CREATE table if not exists QUESTION_ATTEMPT(
	attempt_id int AUTO_INCREMENT PRIMARY key,
    session_id int not null,
    question_id int not null,
    player_answer varchar(100),
    is_correct boolean not null,
    time_taken_seconds int not null,
    attempt_order int not null,
    FOREIGN key (session_id) REFERENCES GAME_SESSION(session_id) on delete CASCADE,
    FOREIGN key (question_id) REFERENCES QUESTION(question_id) on delete CASCADE
);

CREATE table if not exists LEADERBOARD(
	leaderboard_id int AUTO_INCREMENT PRIMARY key,
    player_name varchar(100) not null,
    total_completion_time_seconds int not null,
    level_reached int not null,
    date_achieved datetime default CURRENT_TIMESTAMP,
    rank_position int default 0
);

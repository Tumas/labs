CREATE TABLE "exams" ("id" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "created_at" datetime, "updated_at" datetime, "public" boolean DEFAULT 't', "user_id" integer, "title" varchar(255));
CREATE TABLE "exams_words" ("exam_id" integer, "word_id" integer);
CREATE TABLE "guesses" ("id" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "value" varchar(255), "part" varchar(255), "created_at" datetime, "updated_at" datetime, "user_id" integer, "word_id" integer, "count" integer DEFAULT 0 NOT NULL);
CREATE TABLE "schema_migrations" ("version" varchar(255) NOT NULL);
CREATE TABLE "scores" ("id" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "score" float DEFAULT 0.0 NOT NULL, "user_id" integer, "exam_id" integer, "created_at" datetime, "updated_at" datetime);
CREATE TABLE "tags" ("id" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "title" varchar(255) NOT NULL, "taggable_id" integer, "taggable_type" varchar(255), "created_at" datetime, "updated_at" datetime);
CREATE TABLE "tags_users" ("tag_id" integer, "user_id" integer);
CREATE TABLE "users" ("id" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "created_at" datetime, "updated_at" datetime, "name" varchar(255) DEFAULT 'f' NOT NULL, "password" varchar(255) DEFAULT 'f' NOT NULL);
CREATE TABLE "words" ("id" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "value" varchar(255) NOT NULL, "translation" varchar(255) NOT NULL, "example" text DEFAULT '?', "created_at" datetime, "updated_at" datetime, "user_id" integer);
CREATE UNIQUE INDEX "unique_schema_migrations" ON "schema_migrations" ("version");
INSERT INTO schema_migrations (version) VALUES ('20100424155533');

INSERT INTO schema_migrations (version) VALUES ('20100424155558');

INSERT INTO schema_migrations (version) VALUES ('20100424155707');

INSERT INTO schema_migrations (version) VALUES ('20100424155726');

INSERT INTO schema_migrations (version) VALUES ('20100424155842');

INSERT INTO schema_migrations (version) VALUES ('20100424204007');

INSERT INTO schema_migrations (version) VALUES ('20100424215237');

INSERT INTO schema_migrations (version) VALUES ('20100424222807');

INSERT INTO schema_migrations (version) VALUES ('20100425000850');

INSERT INTO schema_migrations (version) VALUES ('20100425010421');

INSERT INTO schema_migrations (version) VALUES ('20100425012800');

INSERT INTO schema_migrations (version) VALUES ('20100425145120');

INSERT INTO schema_migrations (version) VALUES ('20100425145246');

INSERT INTO schema_migrations (version) VALUES ('20100425151428');

INSERT INTO schema_migrations (version) VALUES ('20100425154309');

INSERT INTO schema_migrations (version) VALUES ('20100425162237');

INSERT INTO schema_migrations (version) VALUES ('20100425190525');

INSERT INTO schema_migrations (version) VALUES ('20100425190824');

INSERT INTO schema_migrations (version) VALUES ('20100425193221');

INSERT INTO schema_migrations (version) VALUES ('20100425213016');

INSERT INTO schema_migrations (version) VALUES ('20100426000934');

INSERT INTO schema_migrations (version) VALUES ('20100426014610');
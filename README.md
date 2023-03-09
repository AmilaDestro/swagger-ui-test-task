# swagger-ui-test-task

This project is aimed to run API tests for PlayerController which can be found by the link:  http://3.68.165.45/swagger-ui.html#/player-controller

The application under test is able to view list of players and perform create, update and delete operations on them.

Players can have only the following roles: supervisor, admin and user.

The supervisor player is created by default and cannot be deleted. Other supervisors cannot be created.

New players can be created only with role 'admin' or 'user'.
New players also have the following properties:
- 'login' - must be unique for each player;
- 'screenName' - must be unique too;
- 'password' - contains letters and numbers
- 'gender' - can be only 'male' or 'female'
- 'age' - must be more than 16 and less than 60.

**Based on the test task requirements my assumptions are the following:**
- the supervisor has the highest level of rights in the app. He can perform any operations on other admins and users. Additionally, he can update himself.
- an admin can create users and other admins, update users and himself.
- an admin cannot update and delete other admins and the supervisor. Also, he cannot delete himself.
- a user can only update himself. He is not able to create/update/delete other users, admins or the supervisor.
- a user cannot delete himself.

**Found bugs:**
1. When creating a new Player the response body contains only id and login fields with values, the rest fields are null.
2. When trying to get a Player with wrong/non-existing id status code is 200 and without a response body, while it is expected to be 404 status code.
3. Attempt to create a new player with already taken by another player login re-writes the existing player.
4. The app allows to create 2 users with the same screenName.
5. User can delete other users.
6. Admin can delete other admins.
7. User can delete himself.
8. User can delete admin.
9. User can update other users besides himself.
10. Admin can update other admins besides himself.
11. User can update admin.

Found bugs are marked by @Severity annotation in tests. Besides I noticed that the rest part of the tests may fail due to slow request execution.
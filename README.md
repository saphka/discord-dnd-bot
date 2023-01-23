# Discord DnD Bot

DnD Character and Games Management Bot

## Building

Requirements

- java version 8 or higher
- maven 3

```shell
mvn clean paackge
```

## Adding to server

Scopes

- bot
- application.commands

Bot permissions

- Send Messages
- Create Public Threads
- Send Messages in Threads
- Embed Links
- Attach Files
- Mention Everyone
- Add Reactions
- Use Slash Commands

Predefined link to add bot to your server -
[link](https://discord.com/api/oauth2/authorize?client_id=1066031966450876558&permissions=311385311296&scope=bot%20applications.commands)

## Slash commands

### `admin`

_Available to users with MANAGE_ROLES permission (e.g. DMs)_

#### `create-game`

Creates a game with slug, name, date and tier.

#### `list-all-characters`

List all players at this server.

#### `list-enrolled`

List enrolled characters.

#### `log-add`

Add entry to game log.

#### `log-list`

Get game log by slug

### `game`

#### `list`

List upcoming games

#### `enroll`

Enroll for a game

#### `un-enroll`

Un-enroll for a game

### `character`

#### `create`

Creates a new character with slug, name, sheet URL and avatar URL

#### `list`

List user's characters

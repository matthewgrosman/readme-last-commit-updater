# README Last Commit Updater

## What is this project?
You can insert some really cool things into a README. One such thing is a widget that sits in your README and displays 
the time since your most recent commit in a certain GitHub repository:

![last-commit](https://img.shields.io/github/last-commit/matthewgrosman/frank-ocean-bot.svg)


This widget is awesome, but the downside is that it can only track one repository. This means if you make more recent 
commits in a different repository than the one you're currently tracking, the "latest commit" widget will not see it 
and will thus not update. This isn't an issue if you only want to track one repo, but if you want to use this widget 
to display your most recent commit to *any* repo, this restriction leads to an annoying process to update it.

This project aims to fix that issue by creating a bot that automatically updates the last commit widget to point to 
the repository you contributed to the most recently. The intention here is to have this run on a scheduled basis 
(maybe every few days) to ensure that the last commit tracker stays mostly up to date.

<br>

## How does this project work?
This project uses the [Java wrapper for the GitHub API](https://github-api.kohsuke.org/). You provide a Personal 
Access Token, and this project will be given the access to view repository history, and push changes to the last 
commit widget markdown in the appropriate README.

<br>

## What's next?
Currently working on turning this into a Lambda function so it can be invoked on a consistent basis without any
manual work involved.
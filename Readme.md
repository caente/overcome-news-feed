overcome-news-feed
=======================

This is the "backend" for overcome-news' API. It listens for the main account tweets, extracts and counts the words
of each one and saves them on the database.

It also has the Cleaner actor which prevents that any twitter source have more than 500 words, by deleting the older ones.

## Hacker News Reader- Facebook Style

![Main App Image](https://abhrajit.com/img/hn_app_mobile_main.png)

In my quest to learn Android programming I joined [Udacity][3] Android Developer Nanodegree Program and it culminated in a capstone project of choice. Being a frequent visitor of [Hacker News][1]. I decided to code a hacker news reader app with the styling of Facebook newsfeed. Will showcase the app here and elaborate on the image extraction logic.

### Libraries Used

- [Hacker News API][2]
- Jsoup\- To parse the weblinks and enable extraction of main image from the link.
- Picasso\- For image processing in Image views.
- Okhttp\- For web network calls.
- Simonvt.Schematic\- For content provider setup.

### Google Services Used

- GCM (Google Task Service)\- For periodic task to load data and keep the app news feed updated in backend.
- Google Analytics\- To measure user activity to named screens

More at: https://abhrajit.com/post/hacker-news-reader-facebook-style/


[1]: https://news.ycombinator.com/news
[2]: https://github.com/HackerNews/API
[3]: http://www.udacity.com
[4]: /img/hn_app_main.gif
[5]: /img/hn_app_splash.png
[6]: /img/hn_app_splash.png

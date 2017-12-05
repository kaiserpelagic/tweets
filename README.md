# tweets

Twitter Streaming API statistics

### Running

In order to run tweets you will need several tokens from your Twitter developer account. Once you've obtained those simply run one of the following commands: 

```scala
sbt "run consumer_key consumer_secret access_token access_token_secret"
```

or if you are already inside an sbt console:

```scala
re-start consumer_key consumer_secret access_token access_token_secret
```

### Architecture

Tweets architecture is broadly broken down into two components. The first component is the twitter streaming client (`TwitterStreaming.scala`), which is responsible for connecting to Twitter's streaming api, parsing the payload into `argonaut.Json`, and enqueing it for the backend process. The backend process (`Pipeline.scala`) consumes the queue, extracts various pieces of data from each tweet, and reduces statistics via the `Stat Monoid`. It's important to note that the streaming client will never be blocked or slowed down by a slow backend process, and each component can be scaled independently to keep up the tweet velocity.

### Considerations

The current implementation uses a circular buffer as a queue between the streaming client and processing backend. This means that if the queue fills up tweets will be silently dropped. Using a bounded queue would allow us to detect when the queue is full and react to it, but doesn't solve the problem of dropping tweets. Ideally we would be able to detect when the backend process is lagging and proactively scale up the service.

Finding the top-k frequent items in an infinite steam is challenging when considering things like memory constraints and unbounded sets of items. The first approach I took was to store *all* item frequencies in a `Map[Item, Long]`, and then sort by frequency to get the top-k. The advantage to this approach is accuracy, but memory usage can be unbounded when dealing with unbounded item sets, and sorting can become impossible. The second approach is based on Lossy Counting described here: https://en.wikipedia.org/wiki/Lossy_Count_Algorithm.



### Http

Tweets also runs an http server (on localhost:8080) so users can see realtime statistics. There is currently a single endpoint:

GET localhost:8080/stats

```json
{
  "tweets_total": 2037,
  "tweets_per_second": 39,
  "tweets_per_minute": 2351,
  "tweets_per_hour": 141118,
  "emojis_total": 23,
  "emoji_percent": 1.13,
  "urls_total": 511,
  "url_percent": 25.09,
  "photo_percent": 0.29,
  "urls_total": 511,
  "photos_total": 6,
  "top_hashtags": [{
    "count": 9,
    "name": "SenSeversen"
  }, {
    "count": 6,
    "name": "BTS"
  }, {
    "count": 4,
    "name": "방탄소년단"
  }, {
    "count": 4,
    "name": "TVPersonality2017"
  }, {
    "count": 4,
    "name": "علي_عبدالله_صالح"
  }],
  "top_domains": [{
    "count": 246,
    "name": "twitter.com"
  }, {
    "count": 28,
    "name": "du3a.org"
  }, {
    "count": 22,
    "name": "fb.me"
  }, {
    "count": 18,
    "name": "ift.tt"
  }, {
    "count": 16,
    "name": "youtu.be"
  }],
  "top_emojis": [{
    "count": 7,
    "name": "sparkles"
  }, {
    "count": 3,
    "name": "exclamation"
  }, {
    "count": 3,
    "name": "fist"
  }, {
    "count": 2,
    "name": "x"
  }, {
    "count": 2,
    "name": "white_check_mark"
  }]
}
```

### Built With

http4s, argonaut, scalaz, and scalaz-streams

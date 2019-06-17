# How to implement your own Data Store

You can find the base file for the Store in `src/base/HarunaStore.js`.  Create a class that extends that and make sure it includes:
* `get name()`
* `async init()`
* `async put(id, value)`
* `async get(id)`
* `_clean(callback)`
> I advise you to grab the latest file from the repo and put it in your repo/project/etc. It'll be much easier.

### `get name()`
This is a getter that defines the name of the store. This value should be changed.

### `async init()`
This is the method that will be called when the store attempts to initialize the database.

### `async put(id, value)`
This method will be called if Haruna wants to put something into the database. ID should be a `string` and value depends. You should check `src/Haruna.js` `_onVote()` method for details regarding the data saved. This may change.

### `async get(id)`
This method will be called when Haruna wants to get something. Should return the value stored.

### `_clean(callback)`
This method is called when the cron job starts. It is responsible for cleaning the database. Please implement your own TTL check logic here. You may use `src/LevelStore.js` as a reference.
The `callback` is a function with an argument which is supposed to be number of entries removed.
Example:
```js
_clean(callback) {
    const sweepTime = Date.now()
    let counter = 0
    for (const [key, val] of this.db) {
        if (val.time === sweepTime) {
            this.db.delete(key)
            counter ++
        }
    }
    callback(counter)
}
```

If you didn't implement any of the core methods, a warning message will show up when you boot Haruna up. In that case, just turn it off and implement what you missed.


#### Extra note
* If you want to change the database error messages, you can extend `_error(err)` method. It doesn't really affect much, besides the default handler is nice already *owo*

* Unfortunately, graceful shutdown hook is not available yet; just hook something to `process.once('beforeExit')` or `process.once('exit')` so you can gracefully shut down the connection.
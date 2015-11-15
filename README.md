Notecards
===
Setup
---
```
# Switch to the correct node version
nvm use

# Build css/js
npm start

# Run app
rlwrap lein figwheel

# Visit http://localhost:8888
```

Questions
---
- figwheel: Why am I getting "Maximum call stack size exceeded" on reload?
- om: Is there a way to notify the action creator of the result of an action? For example, if signup succeeds, I want to redirect to the login page.
- figwheel: Does figwheel support historyApiFallback?
- om: How do I set React children? [This doesn't work.](https://github.com/omcljs/om/issues/291)
- sablono: How can I do `(if pred vector)` with sablono?

TODO
---
- Prevent multiple signups if one is in progress. Same for many other requests.
- devcards
- Focus login password field after signup.
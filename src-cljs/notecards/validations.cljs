(ns notecards.validations)

(defn valid-email? [email]
  (re-find #".@." email))

(defn valid-password? [password]
  (seq password))

(defn valid-passwords? [password confirm]
  (and
    (valid-password? password)
    (= password confirm)))
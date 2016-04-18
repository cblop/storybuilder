(ns storybuilder.handler
  (:require [compojure.core :refer :all]
            [ring.util.response :refer [file-response]]
            [ring.middleware.json :as middleware]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [storybuilder.datastore :refer :all]))

(defroutes handler
  (GET "/" [] (file-response "index.html" {:root "resources/public"}))
  (GET "/tropes/" [] (get-tropes))
  (POST "/tropes/new/" [data] (new-trope data))
  (PUT "/tropes/edit/:id" [id data] (edit-trope id data))
  (DELETE "/tropes/delete/:id" [id] (delete-trope id))
  (GET "/stories/" [] (get-stories))
  (POST "/stories/new/" [data] (new-story data))
  (PUT "/stories/edit/:id" [id data] (edit-story id data))
  (DELETE "/stories/delete/:id" [id] (delete-story id))
  (GET "/characters/" [] (get-characters))
  (GET "/characters/:role/" [role] (get-characters-by-role role))
  (POST "/characters/new/" [data] (new-character data))
  (PUT "/characters/edit/:id" [id data] (edit-character id data))
  (DELETE "/characters/delete/:id" [id] (delete-character id))
  (GET "/objects/" [] (get-objects))
  (POST "/objects/new/" [data] (new-object data))
  (PUT "/objects/edit/:id" [id data] (edit-object id data))
  (DELETE "/objects/delete/:id" [id] (delete-object id))
  (GET "/hello" [] "Hello Wold"))

(def app
  (-> handler
      middleware/wrap-json-response))

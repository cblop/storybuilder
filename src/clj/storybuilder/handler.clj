(ns storybuilder.handler
  (:require [compojure.core :refer :all]
            [ring.util.response :refer [file-response]]
            [ring.middleware.json :as middleware]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            ;; [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [storybuilder.datastore :refer :all]))

(defroutes handler
  (GET "/" [] (file-response "index.html" {:root "resources/public"}))
  (GET "/tropes/" [] (get-tropes))
  (POST "/tropes/new" [& data] (new-trope data))
  ;; (POST "/tropes/edit/" [& data] (edit-trope data))
  (POST "/tropes/edit" [& data] (edit-trope data))
  (POST "/tropes/delete" [id] (delete-trope id))
  (GET "/stories/" [] (get-stories))
  (POST "/stories/new/" [data] (new-story data))
  (POST "/stories/edit/:id" [id data] (edit-story id data))
  (POST "/stories/delete/:id" [id] (delete-story id))
  (GET "/characters/" [] (get-characters))
  (GET "/characters/:role/" [role] (get-characters-by-role role))
  (POST "/characters/new/" [data] (new-character data))
  (POST "/characters/edit/:id" [id data] (edit-character id data))
  (POST "/characters/delete/:id" [id] (delete-character id))
  (GET "/objects/" [] (get-objects))
  (POST "/objects/new/" [data] (new-object data))
  (POST "/objects/edit/:id" [id data] (edit-object id data))
  (POST "/objects/delete/:id" [id] (delete-object id))
  (GET "/hello" [] "Hello Wold"))

(def app
  (-> handler
      wrap-keyword-params
      middleware/wrap-json-params
      middleware/wrap-json-response))

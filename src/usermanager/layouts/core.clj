(ns usermanager.layouts.core
  (:require [hiccup.page :as hp]
            [hiccup.form :as form]))

(defn css
  []
  (slurp "resources/public/assets/css/style.css"))

(defn page-head
  [page-name]
  [:head
   [:meta {:charset  "utf-8"}]
   [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
   [:title (format "%s / %s" "User Manager" page-name)]
   [:style (css)]])

(defn page-header
  [page-name]
  [:header {:id "site-header"}
   [:h2 "User Manager / " [:small [:small page-name]]]
   [:nav #_{:class "cluster"}
    [:a {:href "/"} "Home"]
    [:a {:href "/user/list"
         :title "View the list of users"}
     "List users"]
    [:a {:href "/user/form"
         :title "Fill out form to add new user"}
     "Add user"]
    [:a {:href "/reset"
         :title "Reset change tracking"}
     "Reset"]]])

(defn page-footer
  [changes]
  [:footer {:id "site-footer" :class "stack"}
   [:hr]
   (when changes
     [:p (format "You have made %s change(s) since the last reset!"
                 changes)])])

(defn page-layout
  [{:keys [page-name
           content
           footer]
    :or {page-name ""
         content [:p [:strong "Sorry, no content available."]]
         footer (page-footer nil)}}]
  (list
   (page-head page-name)
   [:body
    [:div {:id "site-top" :class "stack center"}
     (page-header page-name)
     [:main {:id "main"}
      (if (string? content)
        [:p content]
        content)]
     footer]]))

(defn users-table
  [users]
  (let [table-headers ["ID" "Name" "Email" "Department" "Manage"]]
    [:table
     [:thead
      [:tr (for [field table-headers]
             [:th field])]]
     [:tbody
      (if (not-empty users)
        (map (fn user-table-row
               [{:addressbook/keys [id first_name last_name email]
                 department :department/name}]
               (let [name [:a {:href (str "/user/form/" id)}
                           (format "%s %s" first_name last_name)]
                     action [:a {:href (str "/user/delete/" id)} "DELETE"]]
                 [:tr (for [field [id name email department action]]
                        [:td field])]))
             users)
        (list [:tr [:td {:colspan (count table-headers)}
                    "No users exist but "
                    [:a {:href "/user/form"} "new ones can be added"] "."]]))]]))

(defn user-info-form
  [{:addressbook/keys [id first_name last_name email department_id]
    :as _user}
   departments]
  (form/form-to
   {:class "stack"} [:post "/user/save"]

   (form/hidden-field {:id id} "id" id)

   [:div {:class "cluster"}
    (form/label "first_name" "First Name:")
    (form/text-field {:id "first_name" :value first_name}
                     "first_name" first_name)]

   [:div {:class "cluster"}
    (form/label "last_name" "Last Name:")
    (form/text-field {:id "last_name" :value last_name}
                     "last_name" last_name)]

   [:div {:class "cluster"}
    (form/label "email" "Email:")
    (form/text-field {:id "email" :value email}
                     "email" email)]

   [:div {:class "cluster"}
    (form/label "department_id" "Department Id:")
    [:select {:name "department_id" :id "department_id"}
     (form/select-options
      (for [{:department/keys [name id]} departments]
        [name id])
      department_id)]]

   [:hr]
   [:div {:class "cluster"}
    (form/label "submit_button" "Add or Update user:")
    (form/submit-button {:id "submit_button"} "Submit")]))

(def uri->page-name
  {"/" "Home"
   "/user/list" "List users"
   "/user/form" "Add or Update user"
   "/reset" "Reset change tracker"})

(defmulti hydrate-view
  :application/view)

(defmethod hydrate-view :default
  [{:keys [params] :as request}]
  (let [content (or (:content params)
                    (:message params))
        page-matter (merge {:footer (page-footer (:changes params))}
                           (when content {:content content})
                           {:page-name (uri->page-name (:uri request) "")})]
    (page-layout page-matter)))

(defmethod hydrate-view "list"
  [{:keys [params] :as request}]
  (-> request
      (assoc-in [:params :content] (users-table (:users params)))
      (assoc :application/view :default)
      (hydrate-view)))

(defmethod hydrate-view "form"
  [{:keys [params] :as request}]
  (-> request
      (assoc-in [:params :content]
                (user-info-form (:user params)
                                (:departments params)))
      (assoc :application/view :default)
      (hydrate-view)))

(comment
  (users-table
   [{:addressbook/id 1,
     :addressbook/first_name "Sean",
     :addressbook/last_name "Corfield",
     :addressbook/email "sean@worldsingles.com",
     :addressbook/department_id 4,
     :department/name "Development"}])

  (user-info-form {:id 1,
                   :first_name "Sean",
                   :last_name "Corfield",
                   :email "sean@worldsingles.com",
                   :department_id 4,
                   :department/name "Development"}
                  [{:department/id 1 :department/name "foo"}
                   {:department/id 2 :department/name "bar"}
                   {:department/id 3 :department/name "baz"}])

  (hp/html5 (page-layout {:footer (page-footer 42)}))
  )

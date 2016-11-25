
(defn isIn?
    "true if seq contains elm"
    [seq elm]
    (some #(= elm %) seq))

(defn isNotIn?
    "true if seq not contains elm"
    [seq elm]
    (not (some #(= elm %) seq)))

(def S [

    
    (make-scheme
        :id 'has-mode-1
        :conclusion '(has_mode ?E ?P ?M)
        :premises [
            (pm '(Event ?E))
            (pm '(Person ?P))
            (pm '(Mode ?M))
            
            (pm '(occupation ?O ?P))
            (pm '(eval ?O (if (isIn? '("employee" "student" "unemployed") ?O) ?O false)))
            (pm '(month ?MD ?E))
            (pm '(eval ?MD (let [d 9] (if (< ?MD d) ?MD d))))
            (pm '(age ?A ?P))
            (pm '(eval ?A (let [a 42.5] (if (< ?A a) ?A a))))
            (pm '(age ?A ?P))
            (pm '(eval ?A (let [a 17.5] (if (< ?A a) ?A a))))
            (pm '(mode "bike" ?M))
        ]
    )
            
    (make-scheme
        :id 'has-mode-2
        :conclusion '(has_mode ?E ?P ?M)
        :premises [
            (pm '(Event ?E))
            (pm '(Person ?P))
            (pm '(Mode ?M))
            
            (pm '(occupation ?O ?P))
            (pm '(eval ?O (if (isIn? '("employee" "student" "unemployed") ?O) ?O false)))
            (pm '(month ?MD ?E))
            (pm '(eval ?MD (let [d 9] (if (< ?MD d) ?MD d))))
            (pm '(age ?A ?P))
            (pm '(eval ?A (let [a 42.5] (if (< ?A a) ?A a))))
            (pm '(age ?A ?P))
            (pm '(eval ?A (let [a 17.5] (if (>= ?A a) ?A a))))
            (pm '(gender ?G ?P))
            (pm '(eval ?G (if (isIn? '("male") ?G) ?G false)))
            (pm '(occupation ?O ?P))
            (pm '(eval ?O (if (isIn? '("employee" "student") ?O) ?O false)))
            (pm '(mode "bike" ?M))
        ]
    )
            
    (make-scheme
        :id 'has-mode-3
        :conclusion '(has_mode ?E ?P ?M)
        :premises [
            (pm '(Event ?E))
            (pm '(Person ?P))
            (pm '(Mode ?M))
            
            (pm '(occupation ?O ?P))
            (pm '(eval ?O (if (isIn? '("employee" "student" "unemployed") ?O) ?O false)))
            (pm '(month ?MD ?E))
            (pm '(eval ?MD (let [d 9] (if (< ?MD d) ?MD d))))
            (pm '(age ?A ?P))
            (pm '(eval ?A (let [a 42.5] (if (< ?A a) ?A a))))
            (pm '(age ?A ?P))
            (pm '(eval ?A (let [a 17.5] (if (>= ?A a) ?A a))))
            (pm '(gender ?G ?P))
            (pm '(eval ?G (if (isIn? '("male") ?G) ?G false)))
            (pm '(occupation ?O ?P))
            (pm '(eval ?O (if (isIn? '("unemployed") ?O) ?O false)))
            (pm '(mode "car" ?M))
        ]
    )
            
    (make-scheme
        :id 'has-mode-4
        :conclusion '(has_mode ?E ?P ?M)
        :premises [
            (pm '(Event ?E))
            (pm '(Person ?P))
            (pm '(Mode ?M))
            
            (pm '(occupation ?O ?P))
            (pm '(eval ?O (if (isIn? '("employee" "student" "unemployed") ?O) ?O false)))
            (pm '(month ?MD ?E))
            (pm '(eval ?MD (let [d 9] (if (< ?MD d) ?MD d))))
            (pm '(age ?A ?P))
            (pm '(eval ?A (let [a 42.5] (if (< ?A a) ?A a))))
            (pm '(age ?A ?P))
            (pm '(eval ?A (let [a 17.5] (if (>= ?A a) ?A a))))
            (pm '(gender ?G ?P))
            (pm '(eval ?G (if (isIn? '("female") ?G) ?G false)))
            (pm '(mode "public" ?M))
        ]
    )
            
    (make-scheme
        :id 'has-mode-5
        :conclusion '(has_mode ?E ?P ?M)
        :premises [
            (pm '(Event ?E))
            (pm '(Person ?P))
            (pm '(Mode ?M))
            
            (pm '(occupation ?O ?P))
            (pm '(eval ?O (if (isIn? '("employee" "student" "unemployed") ?O) ?O false)))
            (pm '(month ?MD ?E))
            (pm '(eval ?MD (let [d 9] (if (< ?MD d) ?MD d))))
            (pm '(age ?A ?P))
            (pm '(eval ?A (let [a 42.5] (if (>= ?A a) ?A a))))
            (pm '(mode "car" ?M))
        ]
    )
            
    (make-scheme
        :id 'has-mode-6
        :conclusion '(has_mode ?E ?P ?M)
        :premises [
            (pm '(Event ?E))
            (pm '(Person ?P))
            (pm '(Mode ?M))
            
            (pm '(occupation ?O ?P))
            (pm '(eval ?O (if (isIn? '("employee" "student" "unemployed") ?O) ?O false)))
            (pm '(month ?MD ?E))
            (pm '(eval ?MD (let [d 9] (if (>= ?MD d) ?MD d))))
            (pm '(age ?A ?P))
            (pm '(eval ?A (let [a 40.5] (if (< ?A a) ?A a))))
            (pm '(age ?A ?P))
            (pm '(eval ?A (let [a 17.5] (if (>= ?A a) ?A a))))
            (pm '(marital ?MA ?P))
            (pm '(eval ?MA (if (isIn? '("single") ?MA) ?MA false)))
            (pm '(starttime ?ST ?E))
            (pm '(eval ?ST (let [t 922.5] (if (< ?ST t) ?ST t))))
            (pm '(mode "car" ?M))
        ]
    )
            
    (make-scheme
        :id 'has-mode-7
        :conclusion '(has_mode ?E ?P ?M)
        :premises [
            (pm '(Event ?E))
            (pm '(Person ?P))
            (pm '(Mode ?M))
            
            (pm '(occupation ?O ?P))
            (pm '(eval ?O (if (isIn? '("employee" "student" "unemployed") ?O) ?O false)))
            (pm '(month ?MD ?E))
            (pm '(eval ?MD (let [d 9] (if (>= ?MD d) ?MD d))))
            (pm '(age ?A ?P))
            (pm '(eval ?A (let [a 40.5] (if (< ?A a) ?A a))))
            (pm '(age ?A ?P))
            (pm '(eval ?A (let [a 17.5] (if (>= ?A a) ?A a))))
            (pm '(marital ?MA ?P))
            (pm '(eval ?MA (if (isIn? '("single") ?MA) ?MA false)))
            (pm '(starttime ?ST ?E))
            (pm '(eval ?ST (let [t 922.5] (if (>= ?ST t) ?ST t))))
            (pm '(month ?MD ?E))
            (pm '(eval ?MD (let [d 10.5] (if (< ?MD d) ?MD d))))
            (pm '(mode "bike" ?M))
        ]
    )
            
    (make-scheme
        :id 'has-mode-8
        :conclusion '(has_mode ?E ?P ?M)
        :premises [
            (pm '(Event ?E))
            (pm '(Person ?P))
            (pm '(Mode ?M))
            
            (pm '(occupation ?O ?P))
            (pm '(eval ?O (if (isIn? '("employee" "student" "unemployed") ?O) ?O false)))
            (pm '(month ?MD ?E))
            (pm '(eval ?MD (let [d 9] (if (>= ?MD d) ?MD d))))
            (pm '(age ?A ?P))
            (pm '(eval ?A (let [a 40.5] (if (< ?A a) ?A a))))
            (pm '(age ?A ?P))
            (pm '(eval ?A (let [a 17.5] (if (>= ?A a) ?A a))))
            (pm '(marital ?MA ?P))
            (pm '(eval ?MA (if (isIn? '("single") ?MA) ?MA false)))
            (pm '(starttime ?ST ?E))
            (pm '(eval ?ST (let [t 922.5] (if (>= ?ST t) ?ST t))))
            (pm '(month ?MD ?E))
            (pm '(eval ?MD (let [d 10.5] (if (>= ?MD d) ?MD d))))
            (pm '(mode "public" ?M))
        ]
    )
            
    (make-scheme
        :id 'has-mode-9
        :conclusion '(has_mode ?E ?P ?M)
        :premises [
            (pm '(Event ?E))
            (pm '(Person ?P))
            (pm '(Mode ?M))
            
            (pm '(occupation ?O ?P))
            (pm '(eval ?O (if (isIn? '("employee" "student" "unemployed") ?O) ?O false)))
            (pm '(month ?MD ?E))
            (pm '(eval ?MD (let [d 9] (if (>= ?MD d) ?MD d))))
            (pm '(age ?A ?P))
            (pm '(eval ?A (let [a 40.5] (if (< ?A a) ?A a))))
            (pm '(age ?A ?P))
            (pm '(eval ?A (let [a 17.5] (if (>= ?A a) ?A a))))
            (pm '(marital ?MA ?P))
            (pm '(eval ?MA (if (isIn? '("divorced" "married") ?MA) ?MA false)))
            (pm '(mode "public" ?M))
        ]
    )
            
    (make-scheme
        :id 'has-mode-10
        :conclusion '(has_mode ?E ?P ?M)
        :premises [
            (pm '(Event ?E))
            (pm '(Person ?P))
            (pm '(Mode ?M))
            
            (pm '(occupation ?O ?P))
            (pm '(eval ?O (if (isIn? '("employee" "student" "unemployed") ?O) ?O false)))
            (pm '(month ?MD ?E))
            (pm '(eval ?MD (let [d 9] (if (>= ?MD d) ?MD d))))
            (pm '(age ?A ?P))
            (pm '(eval ?A (let [a 40.5] (if (< ?A a) ?A a))))
            (pm '(age ?A ?P))
            (pm '(eval ?A (let [a 17.5] (if (< ?A a) ?A a))))
            (pm '(mode "walk" ?M))
        ]
    )
            
    (make-scheme
        :id 'has-mode-11
        :conclusion '(has_mode ?E ?P ?M)
        :premises [
            (pm '(Event ?E))
            (pm '(Person ?P))
            (pm '(Mode ?M))
            
            (pm '(occupation ?O ?P))
            (pm '(eval ?O (if (isIn? '("employee" "student" "unemployed") ?O) ?O false)))
            (pm '(month ?MD ?E))
            (pm '(eval ?MD (let [d 9] (if (>= ?MD d) ?MD d))))
            (pm '(age ?A ?P))
            (pm '(eval ?A (let [a 40.5] (if (>= ?A a) ?A a))))
            (pm '(marital ?MA ?P))
            (pm '(eval ?MA (if (isIn? '("divorced" "single" "widowed") ?MA) ?MA false)))
            (pm '(mode "car" ?M))
        ]
    )
            
    (make-scheme
        :id 'has-mode-12
        :conclusion '(has_mode ?E ?P ?M)
        :premises [
            (pm '(Event ?E))
            (pm '(Person ?P))
            (pm '(Mode ?M))
            
            (pm '(occupation ?O ?P))
            (pm '(eval ?O (if (isIn? '("employee" "student" "unemployed") ?O) ?O false)))
            (pm '(month ?MD ?E))
            (pm '(eval ?MD (let [d 9] (if (>= ?MD d) ?MD d))))
            (pm '(age ?A ?P))
            (pm '(eval ?A (let [a 40.5] (if (>= ?A a) ?A a))))
            (pm '(marital ?MA ?P))
            (pm '(eval ?MA (if (isIn? '("married") ?MA) ?MA false)))
            (pm '(type ?T ?E))
            (pm '(eval ?T (if (isIn? '("concert") ?T) ?T false)))
            (pm '(age ?A ?P))
            (pm '(eval ?A (let [a 50.5] (if (< ?A a) ?A a))))
            (pm '(mode "car" ?M))
        ]
    )
            
    (make-scheme
        :id 'has-mode-13
        :conclusion '(has_mode ?E ?P ?M)
        :premises [
            (pm '(Event ?E))
            (pm '(Person ?P))
            (pm '(Mode ?M))
            
            (pm '(occupation ?O ?P))
            (pm '(eval ?O (if (isIn? '("employee" "student" "unemployed") ?O) ?O false)))
            (pm '(month ?MD ?E))
            (pm '(eval ?MD (let [d 9] (if (>= ?MD d) ?MD d))))
            (pm '(age ?A ?P))
            (pm '(eval ?A (let [a 40.5] (if (>= ?A a) ?A a))))
            (pm '(marital ?MA ?P))
            (pm '(eval ?MA (if (isIn? '("married") ?MA) ?MA false)))
            (pm '(type ?T ?E))
            (pm '(eval ?T (if (isIn? '("concert") ?T) ?T false)))
            (pm '(age ?A ?P))
            (pm '(eval ?A (let [a 50.5] (if (>= ?A a) ?A a))))
            (pm '(month ?MD ?E))
            (pm '(eval ?MD (let [d 10.5] (if (>= ?MD d) ?MD d))))
            (pm '(mode "public" ?M))
        ]
    )
            
    (make-scheme
        :id 'has-mode-14
        :conclusion '(has_mode ?E ?P ?M)
        :premises [
            (pm '(Event ?E))
            (pm '(Person ?P))
            (pm '(Mode ?M))
            
            (pm '(occupation ?O ?P))
            (pm '(eval ?O (if (isIn? '("employee" "student" "unemployed") ?O) ?O false)))
            (pm '(month ?MD ?E))
            (pm '(eval ?MD (let [d 9] (if (>= ?MD d) ?MD d))))
            (pm '(age ?A ?P))
            (pm '(eval ?A (let [a 40.5] (if (>= ?A a) ?A a))))
            (pm '(marital ?MA ?P))
            (pm '(eval ?MA (if (isIn? '("married") ?MA) ?MA false)))
            (pm '(type ?T ?E))
            (pm '(eval ?T (if (isIn? '("concert") ?T) ?T false)))
            (pm '(age ?A ?P))
            (pm '(eval ?A (let [a 50.5] (if (>= ?A a) ?A a))))
            (pm '(month ?MD ?E))
            (pm '(eval ?MD (let [d 10.5] (if (< ?MD d) ?MD d))))
            (pm '(mode "walk" ?M))
        ]
    )
            
    (make-scheme
        :id 'has-mode-15
        :conclusion '(has_mode ?E ?P ?M)
        :premises [
            (pm '(Event ?E))
            (pm '(Person ?P))
            (pm '(Mode ?M))
            
            (pm '(occupation ?O ?P))
            (pm '(eval ?O (if (isIn? '("employee" "student" "unemployed") ?O) ?O false)))
            (pm '(month ?MD ?E))
            (pm '(eval ?MD (let [d 9] (if (>= ?MD d) ?MD d))))
            (pm '(age ?A ?P))
            (pm '(eval ?A (let [a 40.5] (if (>= ?A a) ?A a))))
            (pm '(marital ?MA ?P))
            (pm '(eval ?MA (if (isIn? '("married") ?MA) ?MA false)))
            (pm '(type ?T ?E))
            (pm '(eval ?T (if (isIn? '("sport") ?T) ?T false)))
            (pm '(mode "walk" ?M))
        ]
    )
            
    (make-scheme
        :id 'has-mode-16
        :conclusion '(has_mode ?E ?P ?M)
        :premises [
            (pm '(Event ?E))
            (pm '(Person ?P))
            (pm '(Mode ?M))
            
            (pm '(occupation ?O ?P))
            (pm '(eval ?O (if (isIn? '("pensioner") ?O) ?O false)))
            (pm '(month ?MD ?E))
            (pm '(eval ?MD (let [d 9] (if (< ?MD d) ?MD d))))
            (pm '(mode "public" ?M))
        ]
    )
            
    (make-scheme
        :id 'has-mode-17
        :conclusion '(has_mode ?E ?P ?M)
        :premises [
            (pm '(Event ?E))
            (pm '(Person ?P))
            (pm '(Mode ?M))
            
            (pm '(occupation ?O ?P))
            (pm '(eval ?O (if (isIn? '("pensioner") ?O) ?O false)))
            (pm '(month ?MD ?E))
            (pm '(eval ?MD (let [d 9] (if (>= ?MD d) ?MD d))))
            (pm '(mode "walk" ?M))
        ]
    )
            

])
        
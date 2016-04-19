(ns storybuilder.parser
  (:require [instaparse.core :as insta]))

(def trope-parser
  (insta/parser
   "trope = (situationdef / alias / norms / sequence / situationdef)+ <'\\n'?>
    alias =
        <whitespace> character <' is also '> character <'\\n'?>

    situation =
        <'When '> event <':'>

    sequence =
        (<'Then '>? (event | obligation) or? <'\\n'?>)+

    situationdef = situation (<'\\n'> <whitespace> norms | <'\\n'> <whitespace whitespace> consequence)+ <'\\n'?>

    or =
        <'\\n' whitespace+ 'Or '> event


    event =
        (character <' is'>? <' '> (move / task)) | give | meet | kill


    give =
        character <' gives '> character <' a ' / ' an '?> item
    meet =
        character <' meets '> character
    kill =
        character <' kills '> character

    norms = permission | obligation

    violation = norms

    character = name

    conditional =
        <' if '> <'they '?> event

    move = mverb <' '> <'to '?> place
    mverb = 'go' / 'goes' / 'leave' / 'leaves' / 'return' / 'returns' / 'at' / 'come' / 'comes'
    verb = word
    place = word


    permission = character <' may '> (move / task) conditional? <'\\n'?>
    obligation = character <' must '> (move / task) (<' before '> deadline)? (<'\\n' whitespace+ 'Otherwise, '> <'the '?> violation)? <'.'?> <'\\n'?>

    deadline = consequence

    task = pverb <' '> role-b / verb / (verb <(' the ' / ' a ' / ' an ')> item) / (verb <' '> item)
    role-b = name

    pverb = 'kill' / 'kills'

    consequence =
        [<'The ' / 'the '>] character <' will '>? <' '> (move / item)
        | [<'The ' / 'the '>] item <' '> verb

    item = [<'The ' / 'the '>] word

    <whitespace> = #'\\s\\s'

    <name> = (<'The ' | 'the '>)? word
    <words> = word (<' '> word)*
    <word> = #'[0-9a-zA-Z\\-\\_\\']*'"
   ))

(defn parse-trope
  [text]
  (insta/add-line-and-column-info-to-metadata
   text
   (trope-parser text)))


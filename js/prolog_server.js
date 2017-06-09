// CodeMirror, copyright (c) by Marijn Haverbeke and others
// Distributed under an MIT license: http://codemirror.net/LICENSE

/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
This  addon  extends  the  "prolog"   mode  to  perform  server-assisted
highlighting.   Server-assisted   highlighting   provides   a   semantic
classification of tokens.
- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */

(function(mod) {
  if (typeof exports == "object" && typeof module == "object") // CommonJS
    mod(require("../../lib/codemirror"));
  else if (typeof define == "function" && define.amd) // AMD
    define(["../../lib/codemirror"], mod);
  else // Plain browser env
    mod(CodeMirror);
})(function(CodeMirror) {
  "use strict";

  var DEFAULT_DELAY = 1000;

  function State(options) {
    if (typeof options == "object") {
      this.uuid = options.uuid;
      this.url  = { change: options.url + "change",
		    tokens: options.url + "tokens",
		    leave:  options.url + "leave"
		  },
      this.delay = options.delay ? options.delay : DEFAULT_DELAY;
      this.generationFromServer = -1;
      this.tmo = null;			/* timeout handle */
    }
  }

  function changeEditor(cm, change) {
    var state = cm.state.prologHighlightServer;

    if ( state == null || state.url == null )
      return;

    if ( state.tmo ) {
      cm.askRefresh();
    }

    $.ajax({ url: state.url.change,
             dataType: "json",
	     contentType: 'application/json',
	     type: "POST",
	     data: JSON.stringify({ uuid: state.uuid,
	                            change: change
				  })
	   });
  }

  function leaveEditor(cm) {
    var state = cm.state.prologHighlightServer;

    if ( state == null || state.url == null )
      return;

    console.log("Leaving CodeMirror "+state.uuid);

    $.ajax({ url: state.url.leave,
	     async: false,  // otherwise it is killed before completion
	     contentType: 'application/json',
	     type: "POST",
	     data: JSON.stringify({ uuid: state.uuid
				  })
	   });
  }

  CodeMirror.defineOption("prologHighlightServer", false, function(cm, val, old) {
    if ( old && old != CodeMirror.Init ) {
      /* FIXME: Unregister the Prolog server */
      cm.off("change", changeEditor);
    }
    if ( val ) {
      cm.state.prologHighlightServer = new State(val);
      cm.askRefresh(50);
      cm.on("change", changeEditor);
      window.addEventListener("unload", function() { leaveEditor(cm); });
    }
  });


  CodeMirror.prototype.askRefresh = function(time) {
    var cm = this;
    var state = cm.state.prologHighlightServer;

    if ( state == null )
      return;

    if ( time == null )
      time = state.delay;

    if ( state.tmo )
      clearTimeout(state.tmo);

    state.tmo = setTimeout(function() { cm.serverAssistedHighlight(); },
			   time);
  };


  CodeMirror.prototype.serverAssistedHighlight = function() {
    var cm = this;
    var state = cm.state.prologHighlightServer;

    if ( state == null || state.url == null || cm.isClean(state.generationFromServer) )
      return;
    state.generationFromServer = cm.changeGeneration();
    $.ajax({ url: state.url.tokens,
	     dataType: "json",
	     data: { uuid: state.uuid },
	     success: function(data, status) {
	       cm.setOption("mode",
			    { name:"prolog",
			      metainfo:data,
			      editor:cm
			    });
	     }
	   });
  }

  CodeMirror.commands.refreshHighlight = function(cm) {
    cm.serverAssistedHighlight();
    return CodeMirror.Pass;
  }

  var syncOnType = { "var": "var",	/* JavaScript Types */
		     "atom": "atom",
		     "qatom": "qatom",
		     "bqstring": "string",
		     "symbol": "atom",
		     "functor": "functor",
		     "tag": "tag",
		     "number": "number",
		     "string": "string",
		     "neg-number": "number",
		     "list_open": "list_open",
		     "list_close": "list_close",
		     "qq_open": "qq_open",
		     "qq_sep": "qq_sep",
		     "qq_close": "qq_close",
		     "dict_open": "dict_open",
		     "dict_close": "dict_close",
		     "brace_term_open": "brace_term_open",
		     "brace_term_close": "brace_term_close",
		     "neck": "neck",
		     "fullstop": "fullstop"
		   };
  var serverSync = { "var": "var",	/* Server Types */
		     "singleton": "var",
		     "atom": "atom",
		     "qatom": "qatom",
		     "string": "string",
		     "codes": "string",
		     "chars": "string",
		     "functor": "functor",
		     "tag": "tag",
		     "control": "atom",
		     "meta": "atom",	/* or number 0-9 */
		     "op_type": "atom",
		     "int": "number",
		     "float": "number",
		     "key": "atom",
		     "sep": "atom",	/* : in dict */

		     "expanded": "expanded",
		     "comment_string":"string",
		     "identifier": "atom",
		     "module": "atom",

		     "head_exported": "atom",
		     "head_public": "atom",
		     "head_extern": "atom",
		     "head_dynamic": "atom",
		     "head_multifile": "atom",
		     "head_unreferenced": "atom",
		     "head_hook": "atom",
		     "head_meta": "atom",
		     "head_constraint": "atom",
		     "head_imported": "atom",
		     "head_built_in": "atom",
		     "head_iso": "atom",
		     "head_def_iso": "atom",
		     "head_def_swi": "atom",
		     "head": "atom",

		     "goal_built_in": "atom",
		     "goal_imported": "atom",
		     "goal_autoload": "atom",
		     "goal_global": "atom",
		     "goal_undefined": "atom",
		     "goal_thread_local": "atom",
		     "goal_dynamic": "atom",
		     "goal_multifile": "atom",
		     "goal_expanded": "atom",
		     "goal_extern": "atom",
		     "goal_recursion": "atom",
		     "goal_meta": "atom",
		     "goal_foreign": "atom",
		     "goal_local": "atom",
		     "goal_constraint": "atom",
		     "goal_not_callable": "atom",

		     "xpce_method": "functor",
		     "xpce_class_builtin":"atom",
		     "xpce_class_lib":"atom",
		     "xpce_class_user":"atom",
		     "xpce_class_undef":"atom",

		     "option_name": "atom",
		     "no_option_name": "atom",

		     "file_no_depends": "atom",
		     "file": "atom",
		     "nofile": "atom",

		     "list_open": "list_open",
		     "list_close": "list_close",
		     "qq_open": "qq_open",
		     "qq_sep": "qq_sep",
		     "qq_close": "qq_close",
		     "qq_type": "atom",
		     "dict_open": "dict_open",
		     "dict_close": "dict_close",
		     "brace_term_open": "brace_term_open",
		     "brace_term_close": "brace_term_close",
		     "neck": "neck",
		     "fullstop": "fullstop",

		     "html": "functor",
		     "entity": "atom",
		     "html_attribute": "functor",
		     "sgml_attr_function": "atom",
		     "http_location_for_id": "atom",
		     "http_no_location_for_id": "atom"
		   };

  /* match the next token.  It is possible that the server has combined
     several tokens into one logical unit.  In that case the token is
     merely a prefix of what the server returned and we try to eat the
     remainder.  Examples are files specifications such as
     library(lists).
  */

  function matchTokenText(stream, content, token) {
    var start;

    if ( content == token )
      return true;

    if ( (start=token.lastIndexOf(content,1)) >= 0 ) {
      var left = token.substring(content.length+start);
      for(var i=0; i<left.length; i++) {
	if ( !stream.eat(left.charAt(i)) ) {
	  stream.backUp(i);
	  return false;
	}
      }
      return true;
    }

    return false;
  }

  /* Enrich the style using the token list from the server.
  */

  CodeMirror.enrichStyle = function(parserConfig, stream, state, type, content, style) {
    if ( state.curTerm != null ) {
      var term = parserConfig.metainfo[state.curTerm];

      if ( !term ) {
	parserConfig.editor.askRefresh();	/* more text added at the end */
	return style;
      }

      var token = term[state.curToken];

      if ( !token ) {
	parserConfig.editor.askRefresh();	/* more text added at the end */
	return style;
      }

      //console.log({at:"call", type:type, content:content, token:token});

      if ( syncOnType[type] ) {
	if ( token.text && content ) {
	  if ( matchTokenText(stream, content, token.text) ) {
	    state.curToken++;
	    return token.type;
	  }

	  return false;
	} else if ( syncOnType[type] == serverSync[token.type] ) {
	  if ( type == "fullstop" ) {
	    state.curTerm++;
	    state.curToken = 0;
	  } else {
	    state.curToken++;
	  }
	  //console.log("--> "+token.type);
	  return token.type;
	} else if ( type == "number" && token.type == "meta" ) {
	  state.curToken++;	/* 0-9 as meta_predicate arguments */
	  return token.type;
	} else if ( type == "neg-number" &&
		    token.text && token.text == "-" ) {
	      /* HACK: A-1 is tokenised as "var" "neg-number" */
	      /* But the server says "var" "atom" "number" */
	      /* Needs operator logic to fix at the client */
	  state.curToken += 2;
	  return "number";
	} else {
	  if ( parserConfig.editor.outOfSync++ == 0 )
	    console.log({type:type, content:content, token:token});
	  parserConfig.editor.askRefresh();
	  return "outofsync";
	}
      }

      if ( content && token.text == content ) {
	state.curToken++;			/* ,; are not synced */
	return token.type;
      }
    }

    return style;
  }

		 /*******************************
		 *	  FETCH ENRICHED	*
		 *******************************/

  CodeMirror.prototype.getEnrichedToken = function(token) {
    if ( token.state.curTerm != null && token.state.curToken != null )
    { var state = this.getOption("mode");

      if ( state.metainfo )
	return state.metainfo[token.state.curTerm][token.state.curToken-1];
    }

    return undefined;
  }

});

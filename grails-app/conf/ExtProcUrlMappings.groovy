class ExtProcUrlMappings {

	static mappings = {
		"/extproc/$action?/$id?"{
			controller = "externalProcess"
			action = action?:"list"
			println controller + action
			constraints {
				// apply constraints here
			}
		}
		
		"/$controller/$action?/$id?"{		
			constraints {
				
			}
		}

		"/"(view:"/index")
		"500"(view:'/error')
	}
}

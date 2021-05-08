import SwiftUI
import shared


struct ContentView: View {
    var viewModel = AuthViewModel.init()
    var body: some View {
        Text("hello").onAppear {

            viewModel.viewState.addObserver { itemsObject in
                switch itemsObject {
                case is ViewState.Success : print("success")
                
                    case is ViewState.EmailDialog : print("show dialog")
                    case is ViewState.Error :
                        let error = (itemsObject as! ViewState.Error)
                        print(error.checkBoxError)
                case is ViewState.ViewType : print((itemsObject as! ViewState.ViewType).description())
                    case .none: print("none")
                    case .some(_): print("some")
                    }
            }
            viewModel.dispatch(command : Command___.ViewType(viewType: PageViewType.login))
            viewModel.dispatch(command: Command___.Submit(username: "amol", password: "root1234", email: "", acceptTerms: true))
        }
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}

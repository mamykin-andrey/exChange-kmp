import shared
import SwiftUI

class ConverterViewModelWrapper : ObservableObject {
    private let viewModel: ConverterViewModel
    @State private var observers: [Closeable] = []
    @Published var state: ConverterScreenState = ConverterScreenState.Loading()
    @Published var shouldScrollToTop = false
    
    init() {
        viewModel = KoinHelper().getViewModel()
    }
    
    func startObserving() {
        observers.append(viewModel.observeState(onEach: { state in
            self.state = state
        }))
        observers.append(viewModel.observeEffect(onEach: { effect in
            if effect is ConverterScreenEffect.CurrentRateChanged {
                self.shouldScrollToTop = true
            }
        }))
    }
    
    func stopObserving() {
        observers.forEach({ $0.onClose() })
        observers = []
    }
    
    func onIntent(
        intent: ConverterScreenIntent
    ) {
        viewModel.onIntent(intent: intent)
    }
}

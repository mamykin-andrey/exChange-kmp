import shared
import SwiftUI

struct CurrencyRatesViewData : Identifiable {
    let id: UUID
    let iconUrl: String
    let title: String
    let amountStr: String
}

struct CurrencyRow: View {
    let iconUrl: String
    let title: String
    @Binding var amountStr: String
    
    var body: some View {
        HStack {
            Image(systemName: "pencil")
                .frame(width: 24)
                .padding([.leading, .trailing], 12)
            
            Text(title)
                .frame(minWidth: 80, alignment: .leading)
            
            Spacer()
            
            TextField("Enter value", text: $amountStr)
                .textFieldStyle(RoundedBorderTextFieldStyle())
                .frame(maxWidth: 70)
        }
        .padding(.vertical, 4)
    }
}

class ConverterViewModelWrapper : ObservableObject {
    private let viewModel: ConverterViewModel
    @State private var observers: [Closeable] = []
    @Published var state: ConverterScreenState = ConverterScreenState.Loading()
    
    init() {
        viewModel = KoinHelper().getViewModel()
    }
    
    func startObserving() {
        observers.append(viewModel.observeState(onEach: { state in
            self.state = state
        }))
        viewModel.startRatesLoading()
    }
    
    func stopObserving() {
        observers.forEach({ $0.onClose() })
        observers = []
    }
}

struct ConverterView: View {
    
    @StateObject var viewModel = ConverterViewModelWrapper()
    
    var body: some View {
        Group {
            if viewModel.state is ConverterScreenState.Loading {
                loadingView()
            } else if let loadedState = viewModel.state as? ConverterScreenState.Loaded {
                loadedView(rates: loadedState.rates)
            } else if viewModel.state is ConverterScreenState.Error {
                errorView()
            }
        }.onAppear {
            viewModel.startObserving()
        }.onDisappear {
            viewModel.stopObserving()
        }
    }
    
    private func loadedView(rates: [CurrencyRateViewData] = []) -> some View {
        List {
            ForEach(rates, id: \.self) { rate in
                CurrencyRow(iconUrl: "", title: rate.code, amountStr: .constant(rate.amountStr))
            }
        }
        .listStyle(PlainListStyle())
    }
    
    private func loadingView() -> some View {
        ProgressView("Loading...")
            .padding()
    }
    
    private func errorView() -> some View {
        VStack {
            Text("Error")
                .font(.headline)
                .foregroundColor(.red)
            Text("Please try again later")
                .foregroundColor(.gray)
        }
        .padding()
    }
}

#Preview {
    ConverterView()
}

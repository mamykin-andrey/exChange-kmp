import shared
import SwiftUI

struct CurrencyRow: View {
    let iconName: String
    let title: String
    @Binding var amountStr: String
    
    var body: some View {
        HStack {
            iconImage(iconName: iconName)
                .resizable()
                .frame(width: 24, height: 24)
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

private func iconImage(iconName: String) -> Image {
    if UIImage(named: iconName) != nil {
        return Image(iconName)
    } else {
        return Image("cur_icon_unknown")
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
    
    @StateObject var viewModel: ConverterViewModelWrapper
    
    init(viewModel: ConverterViewModelWrapper = ConverterViewModelWrapper()) {
        _viewModel = StateObject(wrappedValue: viewModel)
    }
    
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
                let iconName = "cur_icon_\(rate.code.lowercased())"
                CurrencyRow(iconName: iconName, title: rate.code, amountStr: .constant(rate.amountStr))
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

class PreviewConverterViewModelWrapper: ConverterViewModelWrapper {
    override init() {
        super.init()
        self.state = ConverterScreenState.Loaded(rates: [
            CurrencyRateViewData(code: "USD", amountStr: "1.23", cursorPosition: nil),
            CurrencyRateViewData(code: "EUR", amountStr: "0.95", cursorPosition: nil),
            CurrencyRateViewData(code: "JPY", amountStr: "110.0", cursorPosition: nil)
        ])
    }
    override func startObserving() { }
    override func stopObserving() { }
}

#Preview {
    ConverterView(viewModel: PreviewConverterViewModelWrapper())
}

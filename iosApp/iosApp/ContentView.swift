import shared
import SwiftUI

struct CurrencyRow: View {
    let viewData: CurrencyRateViewData
    let onCurrencyOrAmountChanged: (CurrentCurrencyRate) -> Void

    @State private var text: String
    @FocusState private var isFocused: Bool

    init(viewData: CurrencyRateViewData,
         onCurrencyOrAmountChanged: @escaping (CurrentCurrencyRate) -> Void) {
        self.viewData = viewData
        self.onCurrencyOrAmountChanged = onCurrencyOrAmountChanged
        _text = State(initialValue: viewData.amountStr)
    }
    
    var body: some View {
        Button(action: {
            isFocused = true
            notifyChange(text)
        }) {
            HStack(alignment: .center) {
                Image(viewData.code.lowercased())
                    .resizable()
                    .frame(width: 36, height: 36)
                    .padding(4)
                
                Text(viewData.code)
                    .font(.body)
                    .foregroundColor(.primary)
                
                Spacer()
                
                TextField("0", text: $text)
                    .keyboardType(.decimalPad)
                    .multilineTextAlignment(.trailing)
                    .frame(width: 90)
                    .textFieldStyle(RoundedBorderTextFieldStyle())
                    .focused($isFocused)
                    .onChange(of: text) { newValue in
                        if isFocused {
                            notifyChange(newValue)
                        }
                    }
            }
            .padding(16)
            .background(Color(.systemBackground))
        }
        .buttonStyle(PlainButtonStyle())
        .onChange(of: viewData.amountStr) { newAmount in
            if !isFocused && newAmount != text {
                text = newAmount
            }
        }
        .onAppear {
            if viewData.cursorPosition != nil {
                DispatchQueue.main.asyncAfter(deadline: .now() + 0.1) {
                    isFocused = true
                }
            }
        }
    }
    
    private func notifyChange(_ newValue: String) {
        if !newValue.isEmpty {
            onCurrencyOrAmountChanged(
                CurrentCurrencyRate(
                    code: viewData.code,
                    amountStr: newValue,
                    cursorPosition: isFocused ? 0 : nil
                )
            )
        }
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
    
    func onCurrencyOrAmountChanged(
        rate: CurrentCurrencyRate
    ) {
        viewModel.onCurrencyOrAmountChanged(currencyRate: rate)
    }
}

struct ConverterView: View {
    @StateObject var viewModel: ConverterViewModelWrapper
    @Environment(\.presentationMode) private var presentationMode
    
    init(viewModel: ConverterViewModelWrapper = ConverterViewModelWrapper()) {
        _viewModel = StateObject(wrappedValue: viewModel)
    }
    
    var body: some View {
        NavigationView {
            Group {
                if viewModel.state is ConverterScreenState.Loading {
                    loadingView()
                } else if let loadedState = viewModel.state as? ConverterScreenState.Loaded {
                    loadedView(rates: loadedState.rates)
                } else if viewModel.state is ConverterScreenState.Error {
                    errorView()
                }
            }
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .principal) {
                    Text("Rates & Conversions")
                        .font(.headline)
                }
                ToolbarItem(placement: .navigationBarLeading) {
                    Button(action: { presentationMode.wrappedValue.dismiss() }) {
                        Image(systemName: "xmark")
                            .foregroundColor(.primary)
                    }
                }
            }
        }
        .onAppear {
            viewModel.startObserving()
        }
        .onDisappear {
            viewModel.stopObserving()
        }
    }
    
    private func loadedView(rates: [CurrencyRateViewData]) -> some View {
        ScrollView {
            LazyVStack(spacing: 0) {
                ForEach(rates, id: \.code) { rate in
                    CurrencyRow(viewData: rate) { updatedRate in
                        viewModel.onCurrencyOrAmountChanged(rate: updatedRate)
                    }
                    .transition(.opacity)
                }
            }
        }
        .background(Color(.systemGroupedBackground))
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

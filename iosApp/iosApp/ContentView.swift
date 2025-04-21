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
        HStack(alignment: .top) {
            iconImage(currencyCode: viewData.code).resizable().frame(width: 32, height: 32).padding(4)
            
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
                .onTapGesture {
                    isFocused = true
                    notifyChange(text, sendCursorPosition: true)
                }
                .onChange(of: text) { newValue in
                    if isFocused {
                        notifyChange(newValue, sendCursorPosition: false)
                    }
                }
                .onChange(of: viewData.amountStr) { newValue in
                    if !isFocused {
                        text = newValue
                    }
                }
        }
        .padding(16)
        .background(Color(.systemBackground))
        .onTapGesture {
            isFocused = true
            notifyChange(text, sendCursorPosition: true)
        }
        .onChange(of: isFocused) { newValue in
            if newValue {
                notifyChange(text, sendCursorPosition: true)
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
    
    private func notifyChange(_ newValue: String, sendCursorPosition: Bool) {
        onCurrencyOrAmountChanged(
            CurrentCurrencyRate(
                code: viewData.code,
                amountStr: newValue.isEmpty ? "0" : newValue,
                cursorPosition: sendCursorPosition ? 0 : nil
            )
        )
    }
}

private func iconImage(currencyCode: String) -> Image {
    let iconName = "cur_icon_\(currencyCode.lowercased())"
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
            .navigationTitle("Rates & Conversions")
            .navigationBarTitleDisplayMode(.large)
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
            LazyVStack(spacing: 0, pinnedViews: [.sectionHeaders]) {
                ForEach(rates, id: \.code) { rate in
                    CurrencyRow(viewData: rate) { updatedRate in
                        viewModel.onCurrencyOrAmountChanged(rate: updatedRate)
                    }
                    .transition(.opacity)
                }
            }
        }
        .background(Color(.systemGroupedBackground))
        .frame(maxHeight: .infinity, alignment: .top)
        .animation(.easeInOut(duration: 0.3), value: rates)
    }
    
    private func loadingView() -> some View {
        VStack {
            ProgressView("Loading...")
                .padding()
            Spacer()
        }
    }
    
    private func errorView() -> some View {
        VStack {
            VStack {
                Text("Error")
                    .font(.headline)
                    .foregroundColor(.red)
                Text("Please try again later")
                    .foregroundColor(.gray)
            }
            .padding()
            Spacer()
        }
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

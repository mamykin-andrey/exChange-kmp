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

struct ConverterView: View {
    @StateObject var viewModel: ConverterViewModelWrapper
    @State private var showingInfo = false
    @State private var scrollProxy: ScrollViewProxy?
    
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
            .navigationTitle(AppStrings().CONVERTER_TITLE)
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button(action: {
                        showingInfo = true
                    }) {
                        Image(systemName: "info.circle")
                    }
                }
            }
            .sheet(isPresented: $showingInfo) {
                InfoView()
            }
        }
        .onAppear {
            viewModel.startObserving()
            viewModel.onIntent(intent: ConverterScreenIntent.StartLoading())
        }
        .onDisappear {
            viewModel.onIntent(intent: ConverterScreenIntent.StopLoading())
        }
        .onChange(of: viewModel.shouldScrollToTop) { newValue in
            if newValue, let proxy = scrollProxy, let firstRate = (viewModel.state as? ConverterScreenState.Loaded)?.rates.first {
                withAnimation {
                    proxy.scrollTo(firstRate.code, anchor: .top)
                }
                viewModel.shouldScrollToTop = false
            }
        }
    }
    
    private func loadedView(rates: [CurrencyRateViewData]) -> some View {
        ScrollViewReader { proxy in
            ScrollView {
                LazyVStack(spacing: 0, pinnedViews: [.sectionHeaders]) {
                    ForEach(rates, id: \.code) { rate in
                        CurrencyRow(viewData: rate) { updatedRate in
                            viewModel.onIntent(intent: ConverterScreenIntent.CurrencyOrAmountChanged(currencyRate: updatedRate))
                        }
                        .id(rate.code)
                        .transition(.opacity)
                    }
                }
            }
            .background(Color(.systemGroupedBackground))
            .frame(maxHeight: .infinity, alignment: .top)
            .animation(.easeInOut(duration: 0.3), value: rates)
            .onAppear {
                scrollProxy = proxy
            }
        }
    }
    
    private func loadingView() -> some View {
        VStack {
            Spacer()
            ProgressView("Loading...")
                .padding()
            Spacer()
        }
    }
    
    private func errorView() -> some View {
        VStack {
            Spacer()
            Text(AppStrings().ERROR_NETWORK_TITLE)
                .font(.body)
                .padding()
                Button(action: {
                    viewModel.onIntent(intent: ConverterScreenIntent.RetryLoading())
                }) {
                    Text(AppStrings().ERROR_NETWORK_RETRY)
                        .foregroundColor(.blue)
                }
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
            CurrencyRateViewData(code: "JPY", amountStr: "110.0", cursorPosition: nil),
            CurrencyRateViewData(code: "JPY2", amountStr: "110.0", cursorPosition: nil),
            CurrencyRateViewData(code: "JPY3", amountStr: "110.0", cursorPosition: nil),
            CurrencyRateViewData(code: "JPY4", amountStr: "110.0", cursorPosition: nil),
            CurrencyRateViewData(code: "JPY5", amountStr: "110.0", cursorPosition: nil),
            CurrencyRateViewData(code: "JPY6", amountStr: "110.0", cursorPosition: nil),
            CurrencyRateViewData(code: "JPY7", amountStr: "110.0", cursorPosition: nil),
            CurrencyRateViewData(code: "JPY8", amountStr: "110.0", cursorPosition: nil),
            CurrencyRateViewData(code: "JPY9", amountStr: "110.0", cursorPosition: nil),
        ])
//        self.state = ConverterScreenState.Loading()
//        self.state = ConverterScreenState.Error()
    }
    override func startObserving() { }
    override func stopObserving() { }
}

#Preview {
    ConverterView(viewModel: PreviewConverterViewModelWrapper())
}

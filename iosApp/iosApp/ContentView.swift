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

class SampleViewModelWrapper: ObservableObject {
    private let viewModel: ConverterViewModel
    
    @State private var observers: [Closeable] = []
    @Published var rates: [RateEntity] = []
    
    init() {
        let networkClient = RatesNetworkClient()
        let repository = RatesRepository(ratesNetworkClient: networkClient)
        let interactor = ConverterInteractor(ratesRepository: repository)
        viewModel = ConverterViewModel(interactor: interactor)
    }
    
    func startObserving() {
        observers.append(viewModel.observeRates(onEach: { data in
            if let data {
                self.rates = data.rates
            }
        }))
        observers.append(viewModel.observeIsLoading(onEach: { isLoading in }))
        observers.append(viewModel.observeError(onEach: { errorStr in }))
        observers.append(viewModel.observeCurrentRateChanged(onEach: { _ in }))
        viewModel.startRatesLoading()
    }
    
    func stopObserving() {
        observers.forEach({ $0.onClose() })
        observers = []
    }
}

struct CurrencyListView: View {
    @StateObject private var viewModelWrapper = SampleViewModelWrapper()
    
    var body: some View {
        List {
            ForEach(viewModelWrapper.rates, id: \.self) { rate in
                let amountStr = String(format: "%.2f", rate.amount)
                CurrencyRow(iconUrl: "", title: rate.code, amountStr: .constant(amountStr))
            }
        }
        .listStyle(PlainListStyle())
        .onAppear {
            viewModelWrapper.startObserving()
        }.onDisappear {
            viewModelWrapper.stopObserving()
        }
    }
}

#Preview {
    CurrencyListView()
}

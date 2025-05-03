import shared
import SwiftUI

struct InfoView: View {
    @Environment(\.dismiss) private var dismiss
    
    var body: some View {
        NavigationView {
            VStack(alignment: .leading, spacing: 0) {
                Text("Currency exchange rates Kotlin Multiplatform mobile app (Android & iOS). \nClean architecture with shared code for data, domain and presentation layers.\nThe UI is implemented with Jetpack Compose on Android and Swift UI on iOS.")
                    .font(.system(size: 16))
                    .padding(.horizontal, 16)
                    .padding(.vertical, 12)
                
                Link("See the project on GitHub",
                     destination: URL(string: "https://github.com/mamykin-andrey/exChange-kmp")!)
                    .font(.system(size: 16))
                    .foregroundColor(.blue)
                    .padding(.horizontal, 16)
                    .padding(.vertical, 12)
                
                Spacer()
            }
            .navigationTitle("About")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    Button(action: {
                        dismiss()
                    }) {
                        Image(systemName: "chevron.left")
                    }
                }
            }
        }
    }
}

struct InfoView_Previews: PreviewProvider {
    static var previews: some View {
        InfoView()
    }
}


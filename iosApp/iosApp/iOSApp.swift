import shared
import SwiftUI

@main
struct SampleApp: App {
    
    init() {
        KoinHelperKt.doInitKoin()
        // Configure navigation bar appearance globally
        let appearance = UINavigationBarAppearance()
        appearance.configureWithDefaultBackground()
        UINavigationBar.appearance().standardAppearance = appearance
        UINavigationBar.appearance().scrollEdgeAppearance = appearance
    }
    
    var body: some Scene {
        WindowGroup {
            ConverterView()
        }
    }
}

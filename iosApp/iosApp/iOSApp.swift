import shared
import SwiftUI

@main
struct SampleApp: App {
    
    init() {
        KoinHelperKt.doInitKoin()
    }
    
    var body: some Scene {
        WindowGroup {
            ConverterView()
        }
    }
}

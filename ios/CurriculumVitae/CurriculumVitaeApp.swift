/*
 * Copyright (c) 2021. Christian Grach <christian.grach@cmgapps.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import SwiftUI
import common

@main
struct CurriculumVitaeApp: App {
#if DEBUG
    @UIApplicationDelegateAdaptor(AppDelegate.self) var appDelegate
    @Environment(\.scenePhase) private var scenePhase: ScenePhase
    @StateObject private var quickAction = QuickAction()
#endif

    static let koin: Koin_coreKoin = AppModuleKt.doInitKoinNative().koin

    var body: some Scene {
        WindowGroup {
#if DEBUG
            if let action = quickAction.action {
                switch action {
                case .debug:
                    DebugView()
                }
            } else {
                ContentView()
            }
#else
            ContentView()
#endif
        }
#if DEBUG
        .onChange(of: scenePhase) { scenePhase in
            switch scenePhase {
            case .background:
                quickAction.action = nil
                UIApplication.shared.shortcutItems = [
                    UIApplicationShortcutItem(
                        type: QuickActionType.debug.rawValue,
                        localizedTitle: "Debug",
                        localizedSubtitle: nil,
                        icon: UIApplicationShortcutIcon(systemImageName: "ladybug"),
                        userInfo: nil
                    )
                ]
            case .active:
                if let shortcutItem = appDelegate.shortcutItem {
                    quickAction.action = QuickActionType(rawValue: shortcutItem.type)
                } else {
                    quickAction.action = nil
                }
            default: return
            }
        }
#endif
    }
}

#if DEBUG
enum QuickActionType: String {
    case debug
}

final class QuickAction: ObservableObject {
    @Published var action: QuickActionType?

    init(initialValue: QuickActionType? = nil) {
        action = initialValue
    }
}
#endif

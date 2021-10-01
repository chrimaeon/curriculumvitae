// Copyright (c) 2021. Christian Grach <christian.grach@cmgapps.com>
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

import SwiftUI
import common
import UIKit

struct InfoPage: View {
    var version: String = "Version "
        + (Bundle.main.object(forInfoDictionaryKey: "CFBundleShortVersionString") as! String)
        + " ("
        + (Bundle.main.object(forInfoDictionaryKey: kCFBundleVersionKey as String) as! String)
        + ")"
     
    var body: some View {
        VStack(alignment: .leading, spacing: nil) {
            Text("Curriculum Vitae").font(.title)
            Text(version).font(.subheadline)
        
            Text(CopyrightKt.CopyRightText).padding(EdgeInsets(top: 20, leading: 0, bottom: 8, trailing: 0))
            Button("m.cmgapps.com", action: {
                if let url = URL(string: "https://m.cmgapps.com/?utm_source=curriculum_vitae_ios&amp;utm_medium=info_dialog&amp;utm_campaign=cv_app") {
                    UIApplication.shared.open(url)
                }
            }).padding(EdgeInsets(top: 8, leading: 0, bottom: 8, trailing: 0))
            Button("Open Source Licenses", action: {}).padding(EdgeInsets(top: 8, leading: 0, bottom: 8, trailing: 0))
            Button("Project on Github", action: {
                if let url = URL(string: "https://github.com/chrimaeon/curriculumvitae") {
                    UIApplication.shared.open(url)
                }
            }).padding(EdgeInsets(top: 8, leading: 0, bottom: 8, trailing: 0))
        }
    }
}

struct InfoPage_Previews: PreviewProvider {
    static var previews: some View {
        InfoPage()
    }
}

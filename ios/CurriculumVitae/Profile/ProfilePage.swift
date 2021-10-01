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
import MessageUI

struct ProfilePage: View {
    
    @StateObject var viewModel: ProfileViewModel
    
    var body: some View {
        VStack {
            if let profile = viewModel.profile {
                ProfileView(profile: profile)
            } else {
                Spacer()
                ProgressView()
                Spacer()
            }
        }
        .frame(maxHeight: .infinity, alignment: .top)
        .onAppear(perform: {
            viewModel.loadProfile()
        })
    }
}

private struct ProfileView: View {
    
    let profile: Profile
    
    var body: some View {
        VStack {
            AsyncImage(withURL: profile.profileImageUrl)
                .frame(width: 100, height: 100, alignment: .center)
                .clipShape(Circle())
                .shadow(radius: 10)
                .padding()
            Text(profile.name)
                .font(.title)
            Text(profile.address.street)
                .font(.title3)
            Text("\(profile.address.postalCode) \(profile.address.city)")
                .font(.title3)
            Button(profile.email){
                let subject = NSLocalizedString("You are hired!", comment: "Email subject")
                let mailTo = "mailto:\(profile.email)?subject=\(subject.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed)!)"
                if let url =  URL(string: mailTo) {
                    UIApplication.shared.open(url)
                }
            }
            .font(.title3)
            Button(profile.phone){
                if let url =  URL(string: "tel:\(profile.phone)") {
                    UIApplication.shared.open(url)
                }
            }.font(.title3)
            .padding(EdgeInsets(top: 0, leading: 0, bottom: 10, trailing: 0))

            ForEach(profile.intro, id: \.self) {
                Text($0).frame(maxWidth: .infinity, alignment: .topLeading)
            }.padding(EdgeInsets(top: 0, leading: 10, bottom: 4, trailing: 10))
        }.padding()
    }
}

struct ProfileView_Previews: PreviewProvider {
    static var previews: some View {
        ProfileView(profile: Profile(
                        name: "Name",
                        phone: "+12345678",
                        profileImageUrl: "",
                        address: Address(
                            street: "Street 42",
                            city: "Graz",
                            postalCode: "8010"),
                        email: "me@home.com",
                        intro: ["Line1", "Line 2"]))
    }
}


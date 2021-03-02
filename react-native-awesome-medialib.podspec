require "json"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))

Pod::Spec.new do |s|
  s.name         = "react-native-awesome-medialib"
  s.version      = package["version"]
  s.summary      = package["A useful media selector module base on native component"]
  s.homepage     = package["https://github.com/Project5E"]
  s.license      = package["MIT"]
  s.authors      = package["Project5E"]

  s.platforms    = { :ios => "10.0" }
  s.source       = { :git => "https://github.com/Project5E/react-native-awesome-medialib.git", :tag => "#{s.version}" }

  
  s.source_files = "ios/**/*.{h,m,mm,swift}"
  

  s.dependency "React-Core"
end

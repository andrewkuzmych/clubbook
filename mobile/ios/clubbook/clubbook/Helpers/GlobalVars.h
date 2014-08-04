//
//  GlobalVars.h
//  Clubbook
//
//  Created by Andrew on 7/1/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreLocation/CoreLocation.h>

@interface GlobalVars : NSObject {
    //CLLocation* location;
}

//@property(nonatomic,retain)CLLocation *location;
+(GlobalVars*)getInstance;
@end


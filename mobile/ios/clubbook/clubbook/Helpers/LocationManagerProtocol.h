//
//  LocationManagerProtocol.h
//  Clubbook
//
//  Created by Andrew on 7/16/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import <Foundation/Foundation.h>

@protocol LocationManagerProtocol <NSObject>

@optional

- (void)didUpdateLocation;

- (void)didFailUpdateLocation;

@end

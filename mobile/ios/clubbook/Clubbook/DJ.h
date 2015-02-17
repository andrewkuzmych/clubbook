//
//  DJ.h
//  Clubbook
//
//  Created by Anton Semenyuk on 2/17/15.
//  Copyright (c) 2015 clubbook. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface DJ : NSObject

@property (strong, nonatomic) NSString* avatar;
@property (strong, nonatomic) NSString* name;
@property (strong, nonatomic) NSString* email;
@property (strong, nonatomic) NSString* phone;
@property (strong, nonatomic) NSString* music;
@property (strong, nonatomic) NSString* info;
@property (strong, nonatomic) NSMutableArray* photos;
@property (strong, nonatomic) NSString* website;

@end

//
//  NewsData.h
//  Clubbook
//
//  Created by Anton Semenyuk on 1/14/15.
//  Copyright (c) 2015 clubbook. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "Place.h"
#import "DJ.h"

@interface NewsData : NSObject

@property (strong, nonatomic) NSString* createdById;
@property (strong, nonatomic) NSDate* createDate;
@property (strong, nonatomic) NSString* newsDescription;
@property (strong, nonatomic) NSMutableArray* photos;
@property (strong, nonatomic) NSMutableDictionary* tempDownlaodedPhotos;

@property (strong, nonatomic) NSString* type;

@property (strong, nonatomic) NSDate* startTime;
@property (strong, nonatomic) NSDate* endTime;
@property (strong, nonatomic) NSString* shareLink;
@property (strong, nonatomic) NSString* buyLink;

@property (strong, nonatomic) Place* place;
@property (strong, nonatomic) DJ* dj;

@end
